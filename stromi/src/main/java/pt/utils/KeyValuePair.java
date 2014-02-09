/*
*	This file is part of STRoMI.
*
*    STRoMI is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    STRoMI is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with STRoMI.  If not, see <http://www.gnu.org/licenses/>.
*/

package pt.utils;

/**
 * Allows to combine a key and a value in a single class instance
 * @param <K> - Generic Key
 * @param <V> - Generic Value
 */
public class KeyValuePair<K,V> {
    private K key;
    private V value;

    /**
     * Instantiates a KeyValuePair of a K and V generic types
     * @param key - K generic type
     * @param value - V generic type
     */
    public KeyValuePair(K key, V value){
        this.key = key;
        this.value = value;
    }

    /**
     * Gets the key of a KeyValuePair<K,V>
     * @return Returns the key of KeyValuePair
     */
    public K getKey(){return key;}

    /**
     * Gets the value of a KeyValuePair<K,V>
     * @return Returns the value of KeyValuePair
     */
    public V getValue(){return value;}
}
