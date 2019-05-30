package util;

import java.util.Arrays;

/**
 * Represents a configuration of n boolean random variables
 * @author America Chambers
 * @version spring2019
 *
 */
public class BitVector {
	public static final BitVector TRUE = new BitVector(1);
	
	private boolean[] bitString;
	
	/**
	 * Constructs a vector of n bits each initialized to true 
	 * @param n
	 * 		The length of the bit vector
	 */
	public BitVector(int n) {
		this.bitString = new boolean[n];
		for(int i = 0; i < bitString.length; i++) {
			bitString[i] = true;
		}
	}
	
	
	/**
	 * Sets the bit at the specified position to the given value
	 * 
	 * @param position
	 * 				The position of the bit to be set
	 * @param value
	 * 				The value of the bit to be set
	 * 
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int position, boolean value) {
		if(!isValid(position)) {
			throw new IndexOutOfBoundsException();
		}
		if(isValid(position)) {
			bitString[position] = value;
		}
	}

	/**
	 * Returns the value of the bit at the specified position
	 * 
	 * @param position
	 * 				The position of the bit
	 * @return True if the bit is true, false otherwise
	 * 
	 * @throws IndexOutOfBoundsException
	 */
	public boolean get(int position) {
		if(!isValid(position)) {
			throw new IndexOutOfBoundsException();
		}
		return bitString[position];
	}

	/**
	 * Returns the length of the bit set
	 * 
	 * @return The number of bits in the bit set
	 */
	public int length() {
		return bitString.length;
	}
	
	
	/**
	 * Returns a string representation of the bit set
	 * @return A string representation of the bit set
	 */
	@Override
	public String toString() {
		String s = "";
		for(int i = 0; i < bitString.length; i++) {
			s += bitString[i] ? "1" : "0";
		}
		return s;
	}
	
	
	/**
	 * Computes a hash code value for the bit set
	 * @return A hash code value for the bit set
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(bitString);
		return result;
	}

	/**
	 * Determines if two bit sets are equal based upon the value at each position
	 * @return True if both bit sets contain the same value at each position, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BitVector other = (BitVector) obj;
		if (!Arrays.equals(bitString, other.bitString))
			return false;
		return true;
	}

	private boolean isValid(int position) {
		return 0 <= position && position < bitString.length;
	}
}
