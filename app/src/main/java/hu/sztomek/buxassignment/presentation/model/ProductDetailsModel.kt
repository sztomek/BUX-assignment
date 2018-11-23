package hu.sztomek.buxassignment.presentation.model

import android.os.Parcel
import hu.sztomek.buxassignment.domain.model.ProductDetails
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductDetailsModel(val productId: String,
                               val model: ProductDetails? = null,
                               val lastUpdate: Long? = null,
                               val liveUpdateEnabled: Boolean = false) : PersistableModel {
    private companion object : Parceler<ProductDetailsModel> {
        override fun create(parcel: Parcel): ProductDetailsModel {
            return ProductDetailsModel(parcel.readString()!!)
        }

        override fun ProductDetailsModel.write(parcel: Parcel, flags: Int) {
            parcel.writeString(productId)
        }

    }
}