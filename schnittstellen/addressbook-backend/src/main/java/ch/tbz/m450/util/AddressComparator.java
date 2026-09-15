package ch.tbz.m450.util;

import ch.tbz.m450.repository.Address;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Vergleicht Adressen für die Sortierung im Adressbuch.
 *
 * Aufgabe 1: Standard-Sortierung nach Nachname, dann Vorname (alphabetisch,
 * Gross-/Kleinschreibung wird ignoriert).
 *
 * Aufgabe 2: Über den Varargs-Konstruktor kann nach beliebigen zusätzlichen
 * Attributen verglichen werden, z.B. new AddressComparator(SortAttribute.PHONENUMBER).
 * Bei Gleichstand entscheidet das jeweils nächste angegebene Attribut.
 * Null-Werte werden ans Ende sortiert.
 */
public class AddressComparator implements Comparator<Address> {

    public enum SortAttribute {
        LASTNAME, FIRSTNAME, PHONENUMBER, REGISTRATION_DATE, ID
    }

    private final List<SortAttribute> sortAttributes;

    public AddressComparator() {
        this(SortAttribute.LASTNAME, SortAttribute.FIRSTNAME);
    }

    public AddressComparator(SortAttribute... sortAttributes) {
        if (sortAttributes == null || sortAttributes.length == 0) {
            throw new IllegalArgumentException("Mindestens ein Sortierattribut angeben");
        }
        this.sortAttributes = List.of(sortAttributes);
    }

    @Override
    public int compare(Address a1, Address a2) {
        for (SortAttribute attribute : sortAttributes) {
            int result = compareByAttribute(a1, a2, attribute);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    private int compareByAttribute(Address a1, Address a2, SortAttribute attribute) {
        return switch (attribute) {
            case LASTNAME -> compareStrings(a1.getLastname(), a2.getLastname());
            case FIRSTNAME -> compareStrings(a1.getFirstname(), a2.getFirstname());
            case PHONENUMBER -> compareStrings(a1.getPhonenumber(), a2.getPhonenumber());
            case REGISTRATION_DATE -> compareNullable(a1.getRegistrationDate(), a2.getRegistrationDate(), Date::compareTo);
            case ID -> Integer.compare(a1.getId(), a2.getId());
        };
    }

    private int compareStrings(String s1, String s2) {
        return compareNullable(s1, s2, String.CASE_INSENSITIVE_ORDER);
    }

    private <T> int compareNullable(T v1, T v2, Comparator<T> comparator) {
        if (v1 == null && v2 == null) return 0;
        if (v1 == null) return 1;   // nulls ans Ende
        if (v2 == null) return -1;
        return comparator.compare(v1, v2);
    }
}
