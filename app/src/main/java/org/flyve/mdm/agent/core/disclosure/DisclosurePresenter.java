/*
 * Copyright Teclib. All rights reserved.
 *
 * Flyve MDM is a mobile device management software.
 *
 * Flyve MDM is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * Flyve MDM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * ------------------------------------------------------------------------------
 * @author    Rafael Hernandez
 * @copyright Copyright Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/android-mdm-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.mdm.agent.core.disclosure;

import android.app.Activity;

public class DisclosurePresenter implements Disclosure.Presenter {

    private Disclosure.View view;
    private Disclosure.Model model;

    public DisclosurePresenter(Disclosure.View view){
        this.view = view;
        model = new DisclosureModel(this);
    }

    @Override
    public void showError(String message) {
        if(view!=null) {
            view.showError(message);
        }
    }

    @Override
    public void requestDeviceAdmin(Activity activity) {
        model.requestDeviceAdmin(activity);
    }

    @Override
    public void checkDeviceAdminResult(Activity activity, int requestCode, int resultCode) {
        model.checkDeviceAdminResult(activity, requestCode, resultCode);
    }
}
