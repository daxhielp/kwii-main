# kwii

<img width="1024" height="1024" alt="image" src="https://github.com/user-attachments/assets/927580e3-59ba-438b-ab48-c79b9f1d65cf" />

Welcome to **Kwii (ˈkiː.wiː)** — a modern, expressive programming language designed for clarity, productivity, and fun. kwii blends familiar syntax with powerful features, making it easy to learn for newcomers and enjoyable for experienced developers.

---

## ✨ Features

- **Clean, Readable Syntax**  
    Inspired by Python and JavaScript, kwii emphasizes readability and minimal boilerplate.

- **Dynamic Typing**  
    Write code quickly without worrying about type declarations.

- **First-Class Functions**  
    Functions are objects and can be passed, returned, and assigned.

- **Flexible Control Flow**  
    Supports `if`, `else`, `while`, `for`, and more.
---

## 🚀 Installation

### Prerequisites

- **Java 1.8 or higher**  
    Ensure you have Java version 1.8 or above installed. You can check your version with:
    ```sh
    java -version
    ```

### Steps

1. **Clone the Repository**
     ```sh
     git clone https://github.com/daxhielp/kwii-main.git
     cd kwii-main
     ```

2. **Version Specifics**

    **If using Java 11 or higher, you can ignore this**

    Create the compiler files.

    ```sh
    cd src
    javac Kwii.java
    ```

3. **Run Kwii**

    In the src directory:
     ```sh
    
    # Run a kwii script
    java Kwii path/to/your_script.kwii
    ```

    ```sh
    # Start the kwii REPL
    java Kwii
     ```

---

## 📝 Basic Syntax

Kwii is designed to feel familiar if you know Python, JavaScript, or Ruby.

### Hello World

```kwii
print "Hello, world!";
```

### Variables

```kwii
var x = 42;
var name = "kwii";
```

### Functions

```kwii
fun greet(who) {
    print "Hello, " + who + "!";
}
```

### Classes

```kwii
class Tick > Counter {
  init(start) {
     print "one";
  }

  push() {
     super.increment();
  }

}
```

### Classes

```kwii
class Counter {
  init(start) {
     this.value = start;
  }

  increment() {
     this.value = this.value + 1;
  }

  getValue() {
     return this.value;
  }
}
```

### Control Flow

```kwii
if (x > 10) {
    print "x is big";
} else {
    print "x is small";
}

```

### Loops

```kwii
for (int i = 0; i < 5; i = i + 1) {
    print i;
}
```

---

## 🔗 Similarities to Other Languages

- **Python:** Indentation-based blocks, dynamic typing, and simple syntax.
- **JavaScript:** First-class functions, flexible variable assignment.
- **Ruby:** Readable, expressive code with minimalistic inheritance
