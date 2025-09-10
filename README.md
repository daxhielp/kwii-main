# kwii

Welcome to **Kwii** — a modern, expressive programming language designed for clarity, productivity, and fun. kwii blends familiar syntax with powerful features, making it easy to learn for newcomers and enjoyable for experienced developers.

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

- **Rich Standard Library**  
    Includes utilities for strings, collections, math, and file I/O.

- **Cross-Platform**  
    Runs on Windows, macOS, and Linux.

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

    ```console
    javac -cp src Kwii.java
    ```

3. **Run Kwii**

     ```sh
    
    # Run a kwii script
    java -cp src Kwii path/to/your_script.kwii
    ```

    ```sh
    # Start the kwii REPL
    java -cp src Kwii
     ```

---

## 📝 Basic Syntax

Kwii is designed to feel familiar if you know Python, JavaScript, or Ruby.

### Hello World

```kwii
print("Hello, world!")
```

### Variables

```kwii
x = 42
name = "kwii"
```

### Functions

```kwii
def greet(who):
    print("Hello, " + who + "!")
```

### Control Flow

```kwii
if x > 10:
    print("x is big")
else:
    print("x is small")
```

### Loops

```kwii
for i in range(5):
    print(i)
```

---

## 🔗 Similarities to Other Languages

- **Python:** Indentation-based blocks, dynamic typing, and simple syntax.
- **JavaScript:** First-class functions, flexible variable assignment.
- **Ruby:** Readable, expressive code with minimal punctuation.s
