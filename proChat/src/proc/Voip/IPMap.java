package proc.Voip;

import java.util.TreeMap;

/**
 * @author Cody
 * @param <K>
 * @param <V>
 *
 */
public class IPMap<K, V> extends TreeMap<K, V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4537807632284979637L;

	/* (non-Javadoc)
	 * @see java.util.TreeMap#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object o) {
		@SuppressWarnings("unchecked")
		K key = (K) o;
		
		for (K k : this.keySet()) {
			String s = (String) k;
			
			if (s.contains(":"))
				s = s.substring(0, s.indexOf(":"));
			
			if (((String) key).equals(s))
				return true;
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.TreeMap#get(java.lang.Object)
	 */
	@Override
	public V get(Object o) {
		@SuppressWarnings("unchecked")
		K key = (K) o;
		
		for (K k : this.keySet()) {
			String s = (String) k;
			
			if (s.contains(":"))
				s = s.substring(0, s.indexOf(":"));
			
			String a = (String) key;
			
			//System.out.println("A: " + a + " S:" + s);
			
			if (a.equals(s))
				return super.get(k);
		}
		
		return null;
	}

	
}
