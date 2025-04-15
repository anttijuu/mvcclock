# Wall Clocks demo

![Kuvaruutukaappaus sovelluksesta](screenshot.png)

> Tämä aikavyöhykkeiden kellonaikoja näyttävä sovellus on tarkoitettu TOL:n Ohjelmointi 4 -kurssin demoksi. Demon päätarkoitus on näyttää miten vanhentunut tarkkailija -suunnittelumalli (*Observer*) voidaan korvata modernimmalla julkaisija-tilaaja -rakenteella (*Publish-Subscribe*).

## Taustaa

Usein (ainakin ennen vanhaan) käytettiin, varsinkin MVC eli [Model-View-Controller](https://fi.wikipedia.org/wiki/MVC-arkkitehtuuri) -arkkitehtuurin kanssa tarkkailija -suunnittelumallia ([Observer](https://en.wikipedia.org/wiki/Observer_pattern)). Observer-rakenteen avulla Model pystyy ilmoittamaan kaikille käyttöliittymäolioille mallin sisällössä tapahtuneista muutoksista. Käyttöliittymäoliot sitten päivittivät käyttöliittymän tietosisällön näiden ilmoitusten perusteella.

Javassa on (oli) valmiina Observer-suunnittelumallin sekä [Observable](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/Observable.html) luokka että [Observer](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/Observer.html) -rajapinta, mutta kuten kyseisten luokkien dokumentaatiosta voi nähdä, ne ovat **vanhentuneet** (*deprecated*).

> Toki näitä edelleen voi käyttää, tai toteuttaa oman kantaluokan tarkkailun kohteelle (`Observable`, a.k.a `Subject`) ja rajapinnan tarkkailijasta (`Observer`), jos näin haluaa. Suunnittelumalli (*design pattern*) on vanha ja erittäin tunnettu, joten netistä löytyy hyvin materiaalia oman toteutuksen pohjaksi.

Observer:n sijaan kannattaisi siis käyttää jotain muuta tapaa. Mikä se milloinkin on, riippuu tietysti käytettävästä ohjelmointikielestä ja käyttöliittymäohjelmointikirjastosta. Esimerkiksi deklaratiiviset tai reaktiiviset ratkaisut ovat nykyään aika yleisiä (esim. [SwiftUI](https://en.wikipedia.org/wiki/SwiftUI)).

Tässä demossa katsotaan miten vastaava toiminnallisuus saadaan toteutettua käyttäen Javan **Flow** -rajapintoja:

* [Flow.Publisher](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/concurrent/Flow.Publisher.html) - joku sovelluksen komponentti joka **julkaisee** dataa tai muutoksia dataan.
* [Flow.Subscriber](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/concurrent/Flow.Subscriber.html) - joku sovelluksen komponentti joka **tilaa** julkaisijalta tätä dataa, sekä
* [Flow.Subscription](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/concurrent/Flow.Subscription.html) -- **tilaus**, linkki tilaajan ja julkaisijan välillä.

Konkreettisena julkaisijana demosovelluksessa toimii [SubmissionPublisher<T>](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/concurrent/SubmissionPublisher.html), joka toteuttaa `Flow.Publisher` -rajapinnan.


## Demon rakenne

Demon rakenne on hyvin yksinkertainen:

* `ClockTickSource` on **julkaisija**, joka julkaisee tietoa ajan kulumisesta, sekunnin välein.
* `WallClock` on sekä käyttöliittymäelementti että **tilaaja**, joka näyttää ajan etenemistä tietyllä aikavyöhykkeellä.

Sovelluksen käynnistyessä se luo julkaisijan sekä yhden tilaajan, joka näyttää aikaa sillä aikavyöhykkeellä, jolla tietokone sijaitsee (JVM:n tietojen mukaan, jonka se saa käyttöjärjestelmältä).

Julkaisija (`ClockTickSource`) julkaisee tilaajille käytännössä yhtä kokonaislukua sekunnin välein: nykyinen kellonaika millisekunteina, käyttäen Javan `System.currentTimeMillis()` -metodia. Se palauttaa millisekunneissa erotuksen tämän hetken ja tammikuun 1. päivän 1970 välillä UTC-aikaa.

Kukin tilaaja (`WallClock`) sitten tilattuaan julkaisijalta päivityksiä, näyttää kellonajan sillä aikavyöhykkeellä joka tilaajalle annettiin.

Painamalla + -nappia, voit valita listalta minkä aikavyöhykkeen aikaa haluaisit katsoa ja antaa tilaajalle:

![Aikavyöhykkeen valinta](screenshot2.png)

Valinnan jälkeen voit seurata ajan etenemistä myös tällä aikavyöhykkeellä, kuten näet esimerkiksi yltä kuvaruutukaappauksista.

Voit poistaa minkä tahansa tilaajan eli aikavyöhykkeen kellonajan, klikkaamalla sen yhteydessä näkyvää punaista rastia. Tällöin tilaaja peruu tilauksensa käyttäen tilaus -oliota. Mikäli tilaajia ei enää ole (käyttöliittymä on plusnappia lukuunottamatta tyhjä), julkaisija lopettaa ajan etenemisen julkaisemisen. Jos käyttäjä lisää näkymään uuden aikavyöhykkeen (tilaajan), julkaisia aloittaa taas julkaisemisen.


## Kuka tämän teki?

* (c) Antti Juustila, 2025
* Lehtori, INTERACT Research Group
* Faculty of Information Technology and Electrical Engineering
* University of Oulu, Finland



