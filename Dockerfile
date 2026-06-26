# Scala/sbt-Container als Grundlage.
FROM sbtscala/scala-sbt:eclipse-temurin-17.0.15_6_1.12.9_3.3.7

# Bibliotheken für Java-/Scala-Swing-GUI im Linux-Container.
RUN apt-get update && \
    apt-get install -y libxrender1 libxtst6 libxi6 && \
    rm -rf /var/lib/apt/lists/*

# Arbeitsordner innerhalb des Containers.
WORKDIR /mill

# Projekt in den Container kopieren.
ADD . /mill

# Startet das Spiel.
CMD ["sbt", "run"]