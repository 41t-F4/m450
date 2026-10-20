<?php
/**
 * Created by PhpStorm.
 * User: s.kemper
 * Date: 29.03.2022
 * Time: 22:05
 *
 * This class does a lot of things... but has no productive purpose.
 * The only purpose of this class is to teach the basic functions
 * of PHP and how to use them.
 *
 * Implementation (TDD): Nicola Wehrli – die Methoden-Bodies wurden so
 * implementiert, dass alle Testcases in Testcases/Test.php bestanden werden.
 */

class MyFancyClass
{
    /**
     * Kürzt $text auf maximal $length Zeichen (inkl. $ending).
     * - Passt der Text bereits in $length, wird er unverändert zurückgegeben.
     * - Ist $ending länger als $length, ist kein sinnvolles Kürzen möglich -> false.
     * mb_*-Funktionen, damit Umlaute als 1 Zeichen zählen (strlen zählt Bytes!).
     */
    public function shortString($text, $length, $ending = '...') {
        if (mb_strlen($ending) > $length) {
            return false;
        }
        if (mb_strlen($text) <= $length) {
            return $text;
        }
        return mb_substr($text, 0, $length - mb_strlen($ending)) . $ending;
    }

    /**
     * Durchschnitt aller Werte im Array.
     * - Leeres Array -> 0
     * - Numerische Strings zählen wie Zahlen (is_numeric)
     * - Enthält das Array einen nicht-numerischen Wert -> false
     */
    public function calcAverage($values) {
        if (count($values) === 0) {
            return 0;
        }
        $sum = 0;
        foreach ($values as $value) {
            if (!is_numeric($value)) {
                return false;
            }
            $sum += $value;
        }
        return $sum / count($values);
    }

    /**
     * Wandelt ins "Gegenteil":
     * - Array  -> String (implode mit $delimiter)
     * - String -> Array (explode mit $delimiter)
     * - alles andere -> false
     */
    public function getOpposite($value, $delimiter = ',') {
        if (is_array($value)) {
            return implode($delimiter, $value);
        }
        if (is_string($value)) {
            return explode($delimiter, $value);
        }
        return false;
    }
}
