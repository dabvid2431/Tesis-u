package com.tuempresa.stockapp.api

import com.tuempresa.stockapp.models.Product
import com.tuempresa.stockapp.models.Category
import com.tuempresa.stockapp.models.Supplier
import com.tuempresa.stockapp.models.Client
import com.tuempresa.stockapp.models.Purchase
import com.tuempresa.stockapp.models.Sale
import com.tuempresa.stockapp.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Body

interface ApiService {
    // Productos
    @GET("products") fun getProducts(): Call<List<Product>>
    @POST("products") fun createProduct(@Body product: Product): Call<Product>
    @POST("products") fun createProductMap(@Body productMap: Map<String, @JvmSuppressWildcards Any>): Call<Product>
    @PUT("products/{id}") fun updateProduct(@Path("id") id:Int, @Body product: Product): Call<Product>
    @DELETE("products/{id}") fun deleteProduct(@Path("id") id:Int): Call<Unit>

    // Categorías
    @GET("categories") fun getCategories(): Call<List<Category>>
    @POST("categories") fun createCategory(@Body category: Category): Call<Category>
    @PUT("categories/{id}") fun updateCategory(@Path("id") id:Int, @Body category: Category): Call<Category>
    @DELETE("categories/{id}") fun deleteCategory(@Path("id") id:Int): Call<Unit>

    // Proveedores
    @GET("suppliers") fun getSuppliers(): Call<List<Supplier>>
    @POST("suppliers") fun createSupplier(@Body supplier: Supplier): Call<Supplier>
    @PUT("suppliers/{id}") fun updateSupplier(@Path("id") id:Int, @Body supplier: Supplier): Call<Supplier>
    @DELETE("suppliers/{id}") fun deleteSupplier(@Path("id") id:Int): Call<Unit>

    // Clientes
    @GET("clients") fun getClients(): Call<List<Client>>
    @POST("clients") fun createClient(@Body client: Client): Call<Client>
    // Accept a generic map so frontend can send client data without id (avoid duplicate PK errors)
    @POST("clients") fun createClientMap(@Body clientMap: Map<String, @JvmSuppressWildcards Any>): Call<Client>
    @PUT("clients/{id}") fun updateClient(@Path("id") id:Int, @Body client: Client): Call<Client>
    @DELETE("clients/{id}") fun deleteClient(@Path("id") id:Int): Call<Unit>

    // Compras
    @GET("purchases") fun getPurchases(): Call<List<Purchase>>
    @POST("purchases") fun createPurchase(@Body purchase: Purchase): Call<Purchase>
    // Accept a generic map so frontend can send items list: { supplierId, items: [{productId, quantity, price}] }
    @POST("purchases") fun createPurchaseMap(@Body purchaseMap: Map<String, @JvmSuppressWildcards Any>): Call<Purchase>
    @PUT("purchases/{id}") fun updatePurchase(@Path("id") id:Int, @Body purchase: Purchase): Call<Purchase>
    @DELETE("purchases/{id}") fun deletePurchase(@Path("id") id:Int): Call<Unit>

    // Ventas
    @GET("sales") fun getSales(): Call<List<Sale>>
    @POST("sales") fun createSale(@Body sale: Sale): Call<Sale>
    // Accept a generic map so we can send items list: { clientId, items: [{productId, quantity, price}] }
    @POST("sales") fun createSaleMap(@Body saleMap: Map<String, @JvmSuppressWildcards Any>): Call<Sale>
    @PUT("sales/{id}") fun updateSale(@Path("id") id:Int, @Body sale: Sale): Call<Sale>
    @DELETE("sales/{id}") fun deleteSale(@Path("id") id:Int): Call<Unit>

    // Usuarios
    @POST("login")
    fun login(@Body credentials: Map<String, String>): Call<User>

    @POST("users")
    fun createUser(@Body user: User): Call<User>
    @POST("users")
    fun createUserMap(@Body userMap: Map<String, String>): Call<User>
}
