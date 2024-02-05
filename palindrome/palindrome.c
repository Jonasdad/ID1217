#include <stdio.h>   // For printf, fopen, fclose, fgets, and fprintf
#include <string.h>  // For strlen, strdup, and strcasecmp
#include <stdlib.h>  // For malloc
#include <ctype.h>   // For tolower
#include <pthread.h> // For threading
#include "queue.c"   // Include the queue implementation
#include <time.h>    // For benchmarking

//  Max words to allocate enough memory
#define MAX_WORDS 25143
//#define MAX_WORDS 104335

#define PATH "words.txt"
int countedWords = 0;
const char *words[MAX_WORDS];  // Words array
const char *words2[MAX_WORDS]; // Debugging array
queue *bagoftasks;             // Queue of tasks

// File pointers for input output.
FILE *input;
FILE *output;


char *reverse(char *word);
int palindrome(char *word);
int compare404(const void *a, const void *b);
int compare426(const void *a, const void *b);
void *readFile(queue *taskQueue);
void *work();

pthread_mutex_t mutex;

int main(int argc, char *argv[])
{
    // Check if the user has given an argument
    if ((argv[1] == NULL) || (argc > 2))
    {
        printf("Usage: No argument given\n");
        return 1;
    }
    bagoftasks = create_queue();
    input = fopen(PATH, "r");
    output = fopen("output.txt", "w");

    if (input == NULL)
    {
        printf("Error opening file\n");
        return 1;
    }
    readFile(bagoftasks);

    /* Had to sort the array to use bsearch
     as the array was not sorted because of
     special character e.g. &, ', % etc. */
    qsort(words, MAX_WORDS, sizeof(char *), compare404);

    // Mutex and threads initialization
    pthread_mutex_init(&mutex, NULL);
    int numberOfThreads = atoi(argv[1]);
    pthread_t handler[numberOfThreads];
    int *results[numberOfThreads];

    // Benchmarking Start
    clock_t start, end;
    double cpu_time_used;

    start = clock();

    // Create threads
    for (int i = 0; i < numberOfThreads; i++)
    {
        int ret = pthread_create(&handler[i], NULL, &work, NULL);
        if (ret != 0)
        {
            printf("Error: pthread_create failed\n");
            return 1;
        }
    }
    // Join threads
    for (int i = 0; i < numberOfThreads; i++)
    {
        pthread_join(handler[i], (void **)&results[i]);
    }
    // Print results
    for (int i = 0; i < numberOfThreads; i++)
    {
        printf("Thread %d found %d palindromic words\n", i, *results[i]);
        countedWords += *results[i];
        free(results[i]); // Remember to free the result after printing
    }

    printf("Counted palindromic words: %d\n", countedWords);
    // Benchmarking End
    end = clock();
    cpu_time_used = ((double)(end - start)) / CLOCKS_PER_SEC;

    printf("Time taken: %f seconds\n", cpu_time_used);

    fclose(output);
    return 0;
}

 // Thread work function. 
 // Returns the number of palindromic words found by thread
void *work()
{
    int localCount = 0;
    while (1)
    {
        char *word = dequeue(bagoftasks);
        if (word == NULL)
        {
            break; // No more tasks
        }
        // Check if the word is a palindrome. 
        // Palindromes are assumed to trivially be 
        // semordnilaps of themselves.
        if (palindrome(word))
        {
            pthread_mutex_lock(&mutex);
            fprintf(output, "%s\n", word);
            localCount++;
            printf("Palindrome found: %s\n", word);
            pthread_mutex_unlock(&mutex);
        }
        else
        {
            // If not a palindrome - Reverse the word and search in the array
            // in case it is a Semordnilap
            char *rev = reverse(word);
            if (bsearch(&rev, words, MAX_WORDS, sizeof(char *), compare404) != NULL)
            {
                pthread_mutex_lock(&mutex);
                fprintf(output, "%s\n", word);
                localCount++;
                printf("Semordnilap found: %s\n", word);
                pthread_mutex_unlock(&mutex); // Unlock the mutex before continue
                continue;
            }
        }
        free(word); // Remember to free the word after processing
    }
    int *result = malloc(sizeof(int));
    *result = localCount;
    return result;
}

// Remove special characters from a string. 
// Added due to the fact that a lexographically sorted array
// is not sorted in terms of ASCII.
void remove_special_characters(char *str)
{
    int i = 0, j = 0;
    while (str[i])
    {
        if (isalnum(str[i]) || str[i] == ' ')
        {
            str[j] = str[i];
            j++;
        }
        i++;
    }
    str[j] = '\0';
}
// Compare function for qsort and bsearch 
// that doesn't use remove_special_characters
int compare404(const void *a, const void *b)
{
    const char **ia = (const char **)a;
    const char **ib = (const char **)b;
    return strcasecmp(*ia, *ib);
}
// Compare function for qsort and bsearch
// that uses remove_special_characters
int compare426(const void *a, const void *b)
{
    const char **ia = (const char **)a;
    const char **ib = (const char **)b;

    char *copy_a = strdup(*ia);
    char *copy_b = strdup(*ib);

    remove_special_characters(copy_a);
    remove_special_characters(copy_b);

    int result = strcasecmp(copy_a, copy_b);

    free(copy_a);
    free(copy_b);

    return result;
}

// Reverse a string
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
// Read file and add words to the queue and words array
void *readFile(queue *taskQueue)
{
    char *line = malloc(sizeof(char) * 64);
    int index = 0;

    while (fgets(line, 64, input))
    {
        int len = strlen(line);
        if (line[len - 1] == '\n')
        {
            line[len - 1] = '\0'; // Replace newline with null character
        }
        for (int i = 0; line[i]; i++)
        {
            line[i] = tolower(line[i]); // Convert to lowercase
        }
        char *word = strdup(line);
        enqueue(taskQueue, word);
        words[index] = word; // Add word to words array
        words2[index] = word;
        index++;
    }
    free(line);
    fclose(input);
}
// Check if a word is a palindrome
int palindrome(char *word)
{
    char *reversed = reverse(word);
    if (strcasecmp(word, reversed) == 0)
    {
        return 1;
    }
    free(reversed);
    return 0;
}