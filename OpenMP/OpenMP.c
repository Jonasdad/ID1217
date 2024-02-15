#include <omp.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
// Constants
//#define MAX_WORDS 25143
#define MAX_WORDS 57106
//#define MAX_WORDS 104334

#define INPUT_PATH "words3.txt"
#define OUTPUT_PATH "output.txt"
// Global Variables
char* words[MAX_WORDS];
FILE *input;
FILE *output;

// Function Prototypes
void fileReader(FILE *file);
int isPalindrome(char *word);
char* reverse(char *word);
int compare(const void *a, const void *b);

// Main Function
int main(int argc, char *argv[]) {
    int NUM_THREADS = atoi(argv[1]);

    int *semordnilaps = calloc(NUM_THREADS, sizeof(int)); // Initialize an array of counters
    input = fopen(INPUT_PATH, "r");
    if (input == NULL) {
        printf("Error: Could not open file %s\n", INPUT_PATH);
        return 1;  // Return an error code
    }
    fileReader(input);
    output = fopen(OUTPUT_PATH, "w");
    if (output == NULL) {
        printf("Error: Could not open file %s\n", OUTPUT_PATH);
        return 1;  // Return an error code
    }
    output = fopen(OUTPUT_PATH, "w");
    qsort(words, MAX_WORDS, sizeof(char*), compare);
    double start_time = omp_get_wtime();
    #pragma omp parallel num_threads(NUM_THREADS)
    {
        int thread_id = omp_get_thread_num();
        #pragma omp for
        for(int i = 0; i < MAX_WORDS; i++){
            if(isPalindrome(words[i])){
                semordnilaps[thread_id]++;
                {
                    fprintf(output, "%s\n", words[i]);
                }
            }
            else{
                char* reversed = reverse(words[i]);
                if(bsearch(&reversed, words + i, MAX_WORDS-i , sizeof(char*), compare)!= NULL){
                    semordnilaps[thread_id] += 2;
                    {
                        fprintf(output, "%s\n", words[i]);
                        fprintf(output, "%s\n", reversed);
                    }
                }
            }
        }
    }
    double end_time = omp_get_wtime();
    double elapsed_time = end_time - start_time;

    // Print the count of semordnilaps found by each thread
    int total = 0;
    for(int i = 0; i < NUM_THREADS; i++){
        printf("Thread %d found %d semordnilaps\n", i, semordnilaps[i]);
        total += semordnilaps[i];
    }

    // Don't forget to free the allocated memory
    free(semordnilaps);

    printf("Elapsed time: %f seconds\n", elapsed_time);
    fclose(output);
    printf("Number of semordnilaps: %d\n", total);

    return 0;
}

// Function Definitions
int isPalindrome(char *word)
{
    char *reversed = reverse(word);
    if (strcasecmp(word, reversed) == 0)
    {
        return 1;
    }
    free(reversed);
    return 0;
}

char *reverse(char *word)
{
    int length = strlen(word);
    char *reversed = strdup(word); // Allocate space for null character
    for (int i = 0; i < length; i++)
    {
        reversed[i] = word[length - i - 1];
    }
    reversed[length] = '\0'; // Null-terminate the reversed string
    return reversed;
}
int compare(const void *a, const void *b)
{
    const char **ia = (const char **)a;
    const char **ib = (const char **)b;
    return strcasecmp(*ia, *ib);
}
void fileReader(FILE *file){
    char *line = NULL;
    int len = 0;
    int read;
    int i = 0;
    while ((read = getline(&line, &len, file)) != -1) {
        line[strcspn(line, "\n")] = 0;  // Remove newline character
        words[i] = strdup(line);  // Allocate new memory and copy the line
        i++;
    }
    free(line);
    fclose(file);
}