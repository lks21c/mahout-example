package ch3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.Preference;
import org.junit.Test;

/**
 * Created by lks21c on 15. 8. 25.
 */
public class PreferenceTest {

	@Test
	public void test() {
		GenericUserPreferenceArray preferenceArr = new GenericUserPreferenceArray(2);
		preferenceArr.setUserID(0, 1);

		preferenceArr.setItemID(0, 1);
		preferenceArr.setValue(0, 1.0f);

		preferenceArr.setItemID(1, 2);
		preferenceArr.setValue(1, 2.0f);

		Preference pref = preferenceArr.get(0);

		assertEquals(1, pref.getUserID());
		assertEquals(1, pref.getItemID());
		assertTrue(pref.getValue() == 1.0f);
	}
}
