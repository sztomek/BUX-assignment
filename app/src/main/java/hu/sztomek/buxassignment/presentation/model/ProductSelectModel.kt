package hu.sztomek.buxassignment.presentation.model

import android.os.Parcel
import android.os.Parcelable
import hu.sztomek.buxassignment.domain.model.ISelectableProduct

data class ProductSelectModel(val selectedProductId: String?,
                              val selectableProducts: List<ISelectableProduct> = emptyList()) : PersistableModel {

    constructor(parcel: Parcel) : this(parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(selectedProductId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductSelectModel> {
        override fun createFromParcel(parcel: Parcel): ProductSelectModel {
            return ProductSelectModel(parcel)
        }

        override fun newArray(size: Int): Array<ProductSelectModel?> {
            return arrayOfNulls(size)
        }
    }
}