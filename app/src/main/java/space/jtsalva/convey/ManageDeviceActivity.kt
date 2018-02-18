package space.jtsalva.convey

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton

import kotlinx.android.synthetic.main.activity_manage_device.stt_switch
import kotlinx.android.synthetic.main.activity_manage_device.activity_manage_device

class ManageDeviceActivity : Activity(), CompoundButton.OnCheckedChangeListener {

    private companion object {
        const val TAG = "ManageDeviceActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_device)

        stt_switch.setOnCheckedChangeListener(this)
    }

    override fun onCheckedChanged(button: CompoundButton?, isChecked: Boolean) {
        Log.i(TAG, "stt_switch $isChecked")
        if (isChecked) {
            activity_manage_device.background = getDrawable(R.color.green)
        } else {
            activity_manage_device.background = getDrawable(R.color.red)
        }
    }

}
