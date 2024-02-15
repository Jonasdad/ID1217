#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
#include <semaphore.h>

#define PRODUCERS 5
#define MAX_CAPACITY 7
sem_t Fullsemaphore;
sem_t Fillsemaphore;
sem_t Control;
int capacity = 0;

//Function Prototypes
void* produce(void* arg);
void* consume();

int main(int argc, int* argv){

    pthread_t producers[PRODUCERS];
    pthread_t consumer;


    sem_init(&Fullsemaphore, 0, 0);
    sem_init(&Fillsemaphore, 0, 1);
    sem_init(&Control, 0, 1);

    pthread_create(&consumer, NULL, consume, NULL);
    
    for(int i = 0; i < PRODUCERS; i++){
        pthread_create(&producers[i], NULL, produce, (void*)(i+1));
    }

    for(int i = 0; i < PRODUCERS; i++){
        pthread_join(producers[i], NULL);
    }
    pthread_join(consumer, NULL);
    sem_destroy(&Fullsemaphore);
    sem_destroy(&Fillsemaphore);
    return 0;
}


void* consume(){
        while(1){
            sem_wait(&Fullsemaphore);
            if(capacity == MAX_CAPACITY){
                capacity = 0;
                printf("Winnie the Pooh consumes all honey\n", capacity);
                sleep(1);
            }

            sem_post(&Control);
        }
}
void* produce(void* arg){
    int bee_ID = (int)arg;
    while(1){
        sem_wait(&Fillsemaphore);

        if(capacity < MAX_CAPACITY){
            capacity++;
            printf("Bee #%ld is filling the honey pot to %d/7\n", bee_ID, capacity);
            sleep(1);
        }
        if(capacity == MAX_CAPACITY){
            printf("Honey pot is full, Bee #%ld wakes Winnie\n", bee_ID);
            sem_post(&Fullsemaphore);
            sem_wait(&Control);
            sleep(1);
        }
        sem_post(&Fillsemaphore);
    }
}
