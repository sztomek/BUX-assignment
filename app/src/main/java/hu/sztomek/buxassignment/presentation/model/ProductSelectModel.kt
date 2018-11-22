package hu.sztomek.buxassignment.presentation.model

import android.os.Parcel
import hu.sztomek.buxassignment.domain.model.ISelectableProduct
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductSelectModel(val selectedProductId: String?,
                              val selectableProducts: List<ISelectableProduct> = emptyList()) : PersistableModel {

    private companion object : Parceler<ProductSelectModel> {
        override fun create(parcel: Parcel): ProductSelectModel {
            return ProductSelectModel(parcel.readString())
        }

        override fun ProductSelectModel.write(parcel: Parcel, flags: Int) {
            parcel.writeString(selectedProductId)
        }

    }
}