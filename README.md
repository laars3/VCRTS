## M5

Current Tasks & Priorities for M5 (Client-Server Integration)Reference Files: Client_server.zip (for sockets) and CUS1166_Threads.zip (for handling concurrent clients).

Phase 1: Network Layer (Backend)

Server: Multi-threaded socket server that acts as the VC Controller. Receives data, sends an immediate acknowledgment, and holds data in memory.

Client: Socket sender that transmits job/vehicle data from the GUI to the Server.

Phase 2: VCC Admin Dashboard (GUI)

AdminFrame: Build the VC Controller UI so the Admin can view incoming server requests and click "Accept" or "Reject".

Phase 3: GUI Integration & Notifications (Frontend)

Submit Buttons: Reroute ClientFrame and OwnerFrame to use the Socket Client instead of calling local classes like JobStore.

Notifications: Implement pop-ups to show the user if the server accepted or rejected their request.
