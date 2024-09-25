ciao
il progetto parte da il file Master.java che richiama metodi creati nei controllers che hanno i loro modelli di riferimento appunto nella cartella models.Nel file .sql c'è la spiegazione delle tabelle del database. Nella cartella utils invece ci sono dei file per la configurazione dei token e di mySql (credo), e questo è quello che ho trovato:

1.se alla richiesta di registrazione si risponde no il programma va avanti senza un utente loggato
2.i comandi 1, 2 e 3 sembrano funzionare alla perfezione
3.quando si modifica un ordine, inserendo l'id prodotto non viene trovato nessun oggetto nel carrello
4.mentre quando si cancella un ordine l'id dell'ordine è corretto e viene cancellato
5.quando si conferma un ordine non viene effettuato perchè l'id utente non è valido.
6.ci sono vari problemi nella registrazione di un utente ma non riesco a individuare la causa
7.ci sono un centinaio di problemi derivanti da credo una mancata configurazione del classpath(?) anche se funziona ugualmente il programma.