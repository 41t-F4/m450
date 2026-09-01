# Aufgabe 2 – Fehler vs. Mangel & hoher Schaden

> Nennen Sie ein Beispiel eines SW-Fehlers und eines SW-Mangels. Nennen Sie ein Beispiel für einen hohen Schaden bei einem SW-Fehler.

## Begriffe

- **Fehler:** Eine Anforderung wird **nicht erfüllt** – das IST-Verhalten weicht vom SOLL-Verhalten (Spezifikation) ab.
- **Mangel:** Eine Anforderung oder eine **berechtigte Erwartung** wird **nicht angemessen** erfüllt – die Funktion an sich stimmt, aber z. B. die Darstellung oder Benutzbarkeit ist ungenügend.

## Beispiel für einen SW-Fehler

In einem Webshop ist spezifiziert, dass ab CHF 100.– Bestellwert die Versandkosten entfallen. Das System verrechnet aber auch bei einer Bestellung von CHF 150.– noch Versandkosten.
→ Das IST-Verhalten (Versandkosten werden verrechnet) weicht vom SOLL-Verhalten (gratis Versand ab CHF 100.–) ab: **Fehler**.

## Beispiel für einen SW-Mangel

Eine Banking-App berechnet den Kontostand korrekt, zeigt den Betrag im GUI aber abgeschnitten an («1'234.5» statt «1'234.56») oder erst nach mehreren Sekunden Ladezeit.
→ Die Berechnung (Anforderung) ist korrekt erfüllt, aber die berechtigte Erwartung an Darstellung/Reaktionszeit ist nicht angemessen erfüllt: **Mangel**.

## Beispiel für hohen Schaden bei einem SW-Fehler

**Boeing 737 MAX (MCAS-Software):** Ein fehlerhaftes Zusammenspiel von Sensor und Steuerungssoftware drückte die Flugzeugnase automatisch nach unten – zwei Abstürze mit 346 Todesopfern, weltweites Flugverbot und Milliardenschaden für Boeing.

Weitere klassische Beispiele:
- **Ariane 5 (1996):** Zahlenüberlauf bei einer Datentyp-Konvertierung → Rakete zerstörte sich 37 Sekunden nach dem Start selbst (Schaden ca. 500 Mio. USD).
- **Therac-25:** Fehler in der Steuersoftware eines Bestrahlungsgeräts führte zu tödlichen Überdosen bei Patienten.

Diese Beispiele zeigen den Grundsatz zum **Testaufwand**: Je höher der mögliche Schaden (finanziell oder sogar lebensgefährlich), desto ausgiebiger muss getestet werden.
