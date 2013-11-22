package app_kvServer;

import java.util.HashMap;
import java.util.Map;

public class KVDatabase {
	private Map<String, String> store = new HashMap<String, String>();
	
	public boolean containsKey(String key){
		return store.containsKey(key);
	}
	/**
	 * Inserts a key-value pair into the KVServer.
	 * 
	 * @param key
	 *            the key that identifies the given value.
	 * @param value
	 *            the value that is indexed by the given key.
	 * @return true if insertion was successful, else false.

	 */
	public boolean put(String key, String value) {
		store.put(key, value);
		return true;
	}

	/**
	 * Retrieves the value for a given key from the KVServer.
	 * 
	 * @param key
	 *            the key that identifies the value.
	 * @return the value, which is indexed by the given key.
	 */
	public String get(String key) {
		return store.get(key);
	}
	
	public boolean delete(String key){
		if(containsKey(key)) {
			store.remove(key);
			return true;
		}
		else
			return false;
	}
}
