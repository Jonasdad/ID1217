#include <omp.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
// Constants
#define MAX_WORDS 25143
#define INPUT_PATH "words.txt"
#define OUTPUT_PATH "output.txt"
#define NUM_THREADS 4
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
    int semordnilaps = 0;
    input = fopen("words.txt", "r");
    fileReader(input);
    output = fopen("output.txt", "w");
    qsort(words, MAX_WORDS, sizeof(char*), compare);
        #pragma omp parallel for reduction(+:semordnilaps) num_threads(NUM_THREADS)
        for(int i = 0; i < MAX_WORDS; i++){
            if(isPalindrome(words[i])){
                semordnilaps++;
                {
                    printf("%s\n", words[i]);
                    fprintf(output, "%s\n", words[i]);
                }
            }
            else{
                char* reversed = reverse(words[i]);
                if(bsearch(&reversed, words , MAX_WORDS , sizeof(char*), compare)!= NULL){
                    semordnilaps++;
                    {
                        printf("%s\n", words[i]);
                        fprintf(output, "%s\n", words[i]);
                    }
                }
            }
        }
    fclose(output);
    printf("Number of semordnilaps: %d\n", semordnilaps);

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