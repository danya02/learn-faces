#!/usr/bin/python3
import socket
import threading

USERLIST = []
LOGFILE = "./message_log"
try:
    with open("userlist") as o:
        userlist = [i for i in o]
except:
    pass


def syslog(text):
    print("LOG:", text)


serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serversocket.bind((socket.gethostname(), 1337))
serversocket.listen(5)


def server_thread(socketobj):
    r = socketobj.makefile("r")
    w = socketobj.makefile("w")
    buf = ""
    symb = ""
    while not symb == "\n":
        symb = r.read()
        print(symb, end="")
        buf += symb
    if buf == "REG\n":
        syslog("User wants to be added to user list.")
        w.write("0")
        w.flush()
        buf = ""
        symb = ""
        while not symb == "\n":
            symb = r.read(1)
            buf += symb
        USERLIST += [buf.split("::")[0]]
        syslog("User used UUID of " + buf.split("::")[0])
    else:
        syslog("User " + buf.split("::")[0] + "sent a message.")
        if buf.split("::")[0] not in USERLIST:
            syslog("But user " + buf.split("::")[0] + " was not in the userlist, so we refused.")
            w.write("E")
            w.flush()
            w.close()
            r.close()
            socketobj.close()
        else:
            syslog("And user " + buf.split("::")[0] + " was in the userlist, so we agreed.")
            with open(LOGFILE, "a") as o:
                w.write("0")
                w.flush()
                symb = None
                while not symb == "":
                    o.write(symb)
                    symb = r.read(1)
        r.close()
        w.close
        socketobj.close()


while True:
    clientsocket, address = serversocket.accept()
    ct = threading.Thread(target=server_thread, args=(clientsocket,))
    ct.run()
