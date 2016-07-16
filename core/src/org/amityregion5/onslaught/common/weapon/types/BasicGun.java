package org.amityregion5.onslaught.common.weapon.types;

import org.amityregion5.onslaught.common.weapon.data.WeaponData;

public class BasicGun extends AbstractWeapon<WeaponData> {
	@Override
	protected Class<WeaponData> getDataClass() {
		return WeaponData.class;
	}
}
