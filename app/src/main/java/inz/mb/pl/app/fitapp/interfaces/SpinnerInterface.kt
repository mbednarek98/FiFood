package inz.mb.pl.app.fitapp.interfaces

import inz.mb.pl.app.fitapp.adapter.spinner_adapters.SpinnerImageAdapter
import inz.mb.pl.app.fitapp.adapter.spinner_adapters.SpinnerDescriptionAdapter
import inz.mb.pl.app.fitapp.adapter.spinner_adapters.SpinnerTextAdapter

interface SpinnerInterface {
    fun assignNewSpinnerAdapter(spinner : androidx.appcompat.widget.AppCompatSpinner, idTitleArray : Int, idDescArray : Int, idDefault : Int) = with(spinner){
        adapter = SpinnerDescriptionAdapter(
            resources.getStringArray(idTitleArray).toList(),
            resources.getStringArray(idDescArray).toList()
        )
        setSelection(idDefault)
    }

    fun assignNewSpinnerAdapter(spinner : androidx.appcompat.widget.AppCompatSpinner, idTitleArray : Int, idDescArray : Int) = with(spinner){
        adapter = SpinnerImageAdapter(
            context,
            resources.getStringArray(idTitleArray).toList(),
            resources.getStringArray(idDescArray).toList())
        setSelection(0)
    }

    fun assignTextSpinnerAdapter(spinner : androidx.appcompat.widget.AppCompatSpinner, idTitleArray : Int) = with(spinner){
        adapter = SpinnerTextAdapter(resources.getStringArray(idTitleArray).toList())
    }

}