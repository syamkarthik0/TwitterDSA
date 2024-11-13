class Node {
  constructor(tweet) {
    this.tweet = tweet;
    this.next = null;
    this.prev = null;
  }
}

export class LinkedList {
  constructor() {
    this.head = null;
    this.tail = null;
    this.size = 0;
    this.tweetIds = new Set(); // Track tweet IDs to prevent duplicates
  }

  append(tweet) {
    // Skip if tweet already exists
    if (this.tweetIds.has(tweet.id)) {
      return;
    }

    const newNode = new Node(tweet);
    this.tweetIds.add(tweet.id);
    this.size++;

    if (!this.head) {
      this.head = newNode;
      this.tail = newNode;
      return;
    }

    newNode.prev = this.tail;
    this.tail.next = newNode;
    this.tail = newNode;
  }

  prepend(tweet) {
    // Skip if tweet already exists
    if (this.tweetIds.has(tweet.id)) {
      return;
    }

    const newNode = new Node(tweet);
    this.tweetIds.add(tweet.id);
    this.size++;

    if (!this.head) {
      this.head = newNode;
      this.tail = newNode;
      return;
    }

    newNode.next = this.head;
    this.head.prev = newNode;
    this.head = newNode;
  }

  appendList(tweets) {
    tweets.forEach((tweet) => this.append(tweet));
  }

  prependList(tweets) {
    [...tweets].reverse().forEach((tweet) => this.prepend(tweet));
  }

  clear() {
    this.head = null;
    this.tail = null;
    this.size = 0;
    this.tweetIds.clear();
  }

  toArray() {
    const array = [];
    let current = this.head;
    while (current) {
      array.push(current.tweet);
      current = current.next;
    }
    return array;
  }
}
