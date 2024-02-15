#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
#include <semaphore.h>

#define PRODUCERS 5 // Number of producer threads (bees)
#define MAX_CAPACITY 7 // Maximum capacity of the honey pot
sem_t Fullsemaphore; // Semaphore indicating if the honey pot is full
sem_t Fillsemaphore; // Semaphore controlling access to the honey pot
sem_t Control; // Semaphore controlling the order of operations
int capacity = 0; // Current capacity of the honey pot

// Function Prototypes
void* produce(void* arg); // Function for the producer threads (bees)
void* consume(); // Function for the consumer thread (Winnie the Pooh)

int main(int argc, int* argv){
    pthread_t producers[PRODUCERS]; // Producer threads (bees)
    pthread_t consumer; // Consumer thread (Winnie the Pooh)

    // Initialize the semaphores
    sem_init(&Fullsemaphore, 0, 0);
    sem_init(&Fillsemaphore, 0, 1);
    sem_init(&Control, 0, 1);

    // Create the consumer thread (Winnie the Pooh)
    pthread_create(&consumer, NULL, consume, NULL);
    
    // Create the producer threads (bees)
    for(int i = 0; i < PRODUCERS; i++){
        pthread_create(&producers[i], NULL, produce, (void*)(i+1));
    }

    // Wait for all the producer threads (bees) to finish
    for(int i = 0; i < PRODUCERS; i++){
        pthread_join(producers[i], NULL);
    }

    // Wait for the consumer thread (Winnie the Pooh) to finish
    pthread_join(consumer, NULL);

    // Destroy the semaphores
    sem_destroy(&Fullsemaphore);
    sem_destroy(&Fillsemaphore);

    return 0;
}

// Function for the consumer thread (Winnie the Pooh)
void* consume(){
    while(1){
        // Wait until the honey pot is full
        sem_wait(&Fullsemaphore);

        // Consume all the honey in the honey pot
        if(capacity == MAX_CAPACITY){
            capacity = 0;
            printf("Winnie the Pooh consumes all honey\n", capacity);
            sleep(1);
        }

        // Allow the producer threads (bees) to start filling the honey pot again
        sem_post(&Control);
    }
}

// Function for the producer threads (bees)
void* produce(void* arg){
    int bee_ID = (int)arg; // ID of the current bee

    while(1){
        // Wait for access to the honey pot
        sem_wait(&Fillsemaphore);

        // Fill the honey pot
        if(capacity < MAX_CAPACITY){
            capacity++;
            printf("Bee #%ld is filling the honey pot to %d/7\n", bee_ID, capacity);
            sleep(1);
        }

        // If the honey pot is full, wake up Winnie the Pooh
        if(capacity == MAX_CAPACITY){
            printf("Honey pot is full, Bee #%ld wakes Winnie\n", bee_ID);
            sem_post(&Fullsemaphore);
            sem_wait(&Control);
            sleep(1);
        }

        // Release access to the honey pot
        sem_post(&Fillsemaphore);
    }
}