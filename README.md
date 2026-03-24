## Inventory & ROI Tracker

A Java-based financial tool built to manage inventory and a sales record with a focus on investment performance. This app includes more then simple lists by incorporating real-time market simulations and automated ROI calculations.

## Why I Built This
I bult this to replaced my manual spreadsheets with a multi-threaded engine to eliminate the friction of static data entry and provide real-time, automated portfolio insights.

## Key Technical Features
* Real-Time Price Simulation: Uses `ScheduledExecutorService` to run asynchronous background threads that simulate market price fluctuations (±2%) without interrupting the UI (multi threading).
* Data Persistence: Built a File I/O system using CSV to ensure inventory and sale history are saved and recoverable across sessions.
* MVC Architecture: Applied MVC design pattern to keep business logic separate from the JavaFX UI keeping it organized .
* Interactive Sale Logic: Includes a double-click feature that triggers logic to "sell" an item, automatically moving items from Active Inventory to the Sale Log while calculating ROI.

## Class Diagram
The system uses an abstract base class to manage shared product attribute for both live inventory and historical records.
<img width="494" height="371" alt="image" src="https://github.com/user-attachments/assets/25b70982-87a0-45ec-a67f-e87216f86dc5" />



