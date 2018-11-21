package hu.sztomek.buxassignment.presentation.model

import android.os.Parcel
import android.os.Parcelable
import hu.sztomek.buxassignment.domain.model.ProductDetails

data class ProductDetailsModel(val productId: String,
                               val model: ProductDetails? = null) : PersistableModel {
    constructor(parcel: Parcel) : this(parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductDetailsModel> {
        override fun createFromParcel(parcel: Parcel): ProductDetailsModel {
            return ProductDetailsModel(parcel)
        }

        override fun newArray(size: Int): Array<ProductDetailsModel?> {
            return arrayOfNulls(size)
        }
    }
}