#include <stdio.h>
#include <strings.h>
#include <stdlib.h>
#include <pthread.h>
#include <semaphore.h>
#include <unistd.h>

#define MAX_BABY_BIRDS 5 // Number of baby birds
#define MAX_WORMS 20 // Maximum number of worms mama bird can gather
int worms = 10; // Initial number of worms
sem_t Eatsemaphore; // Semaphore for eating
sem_t Fillsemaphore; // Semaphore for filling

// Function Prototypes
void* consume(void* arg); // Function for the baby birds to eat
void* produce(); // Function for the mama bird to gather worms

int main(int argc, int* argv){
    pthread_t baby_birds[MAX_BABY_BIRDS]; // Threads for baby birds
    pthread_t mama_bird[1]; // Thread for mama bird

    // Initialize the semaphores
    sem_init(&Eatsemaphore, 0, 1);
    sem_init(&Fillsemaphore, 0, 0);

    // Create threads for baby birds
    for(int i = 0; i < MAX_BABY_BIRDS; i++){
        pthread_create(&baby_birds[i], NULL, consume, (void*)(i+1));
    }

    // Create thread for mama bird
    pthread_create(&mama_bird[1], NULL, produce, NULL); 
    
    // Wait for all baby bird threads to finish
    for(int i = 0; i < MAX_BABY_BIRDS; i++){
        pthread_join(baby_birds[i], NULL);
    }

    // Wait for mama bird thread to finish
    pthread_join(mama_bird[1], NULL);

    // Destroy the semaphores
    sem_destroy(&Eatsemaphore);
    sem_destroy(&Fillsemaphore);

    return 0;
}

// Function for the baby birds to eat
void* consume(void* arg){
    int bird_ID = (int) arg; // ID of the current baby bird
    while(1){
        sem_wait(&Eatsemaphore); // Acquire the semaphore

        // If there are worms, eat one
        if(worms > 0){
            int worms_left = --worms;
            printf("Baby bird #%ld ate a worm, %d left\n", bird_ID, worms_left);
            sleep(1);
            sem_post(&Eatsemaphore); // Release the semaphore
        }
        // If there are no worms, chirp for mama bird
        else{
            printf("Baby bird #%d is chirping\n", bird_ID);
            sem_post(&Fillsemaphore); // Signal mama bird to gather worms
            sleep(1);
        }
    }
}

// Function for the mama bird to gather worms
void* produce(){
    while(1){
        sem_wait(&Fillsemaphore); // Wait until a baby bird chirps

        // If there are no worms, gather some
        if(worms == 0){
            srand(time(NULL));
            worms = rand() % MAX_WORMS + 1;
            printf("Mama bird gathered %d worms\n", worms);
            sleep(1);
        }
        sem_post(&Eatsemaphore); // Signal the baby birds to eat
    }
}