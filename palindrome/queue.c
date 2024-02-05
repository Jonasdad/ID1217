#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include <pthread.h>
typedef struct node {
    void* data;
    struct node* next;
} node;

typedef struct queue {
    node* head;
    node* tail;
    pthread_mutex_t lock;
} queue;

queue* create_queue() {
    queue* q = malloc(sizeof(queue));
    if (q == NULL) {
        return NULL;
    }
    q->head = q->tail = NULL;
    pthread_mutex_init(&q->lock, NULL);
    return q;
}

void enqueue(queue* q, void* data) {
    node* new_node = malloc(sizeof(node));
    if (new_node == NULL) {
        return;
    }
    new_node->data = strdup(data);
    new_node->next = NULL;

    if (q->tail == NULL) {
        q->head = q->tail = new_node;
    } else {
        q->tail->next = new_node;
        q->tail = new_node;
    }
}

void* dequeue(queue* q) {
    pthread_mutex_lock(&q->lock);
    if (q->head == NULL) {
        pthread_mutex_unlock(&q->lock);
        return NULL;
    }
    node* temp = q->head;
    void* data = temp->data;
    q->head = q->head->next;
    if (q->head == NULL) {
        q->tail = NULL;
    }
    pthread_mutex_unlock(&q->lock);

    free(temp);
    return data;
}