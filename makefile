JC = javac
JFLAGS = -g
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	./Detail/Defines.java \
	./Detail/PeerConfig.java \
   ./Detail/PeerInfo.java \
   ./Detail/PieceInfo.java \
   ./Detail/Tokens.java \
   ./Handler/Bit.java \
   ./Handler/Log.java \
   ./Handler/PieceController.java \
   ./Msg/Msg.java \
   ./Msg/MsgId.java \
   ./Msg/MsgInfo.java \
   ./Msg/ShakeMsg.java \
   ./Threads/ChokeThread.java \
   ./Threads/LogThread.java \
   ./Threads/MessageThread.java \
   ./Threads/PeerThread.java \
   ./Threads/PieceThread.java \
   ./Threads/ServerThread.java \
   ./Threads/UnChokeThread.java \
   ./Main/InitController.java \
   peerProcess.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class

