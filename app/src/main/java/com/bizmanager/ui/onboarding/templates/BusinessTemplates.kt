package com.bizmanager.ui.onboarding.templates

object BusinessTemplates {

    data class TemplateProduct(val name: String, val price: Double)

    val restaurantCategories = listOf(
        "Shawaya" to listOf(
            TemplateProduct("Shawaya ¼", 120.0),
            TemplateProduct("Shawaya ½", 230.0),
            TemplateProduct("Shawaya ¾", 340.0),
            TemplateProduct("Shawaya Full", 450.0)
        ),
        "Chicken" to listOf(
            TemplateProduct("Chicken Al Faham", 220.0),
            TemplateProduct("Chicken Barbecue", 250.0)
        ),
        "Rice & Meals" to listOf(
            TemplateProduct("Mandi Rice", 90.0),
            TemplateProduct("Biryani", 150.0)
        ),
        "Burgers" to listOf(
            TemplateProduct("Chicken Burger", 100.0),
            TemplateProduct("Zinger Burger", 120.0)
        ),
        "Snacks" to listOf(
            TemplateProduct("Samosa", 15.0),
            TemplateProduct("Spring Roll", 30.0)
        ),
        "French Fries" to listOf(
            TemplateProduct("Fries Small", 50.0),
            TemplateProduct("Fries Medium", 80.0),
            TemplateProduct("Fries Full", 120.0)
        ),
        "Drinks" to listOf(
            TemplateProduct("Soft Drink 500ml", 40.0),
            TemplateProduct("Water 1L", 20.0)
        ),
        "Desserts" to listOf(
            TemplateProduct("Cake Slice", 60.0),
            TemplateProduct("Ice Cream", 50.0)
        ),
        "Coffee & Tea" to listOf(
            TemplateProduct("Black Coffee", 30.0),
            TemplateProduct("Tea", 20.0)
        )
    )

    val groceryCategories = listOf(
        "Vegetables" to listOf(
            TemplateProduct("Tomato 1kg", 30.0),
            TemplateProduct("Onion 1kg", 40.0),
            TemplateProduct("Potato 1kg", 35.0)
        ),
        "Fruits" to listOf(
            TemplateProduct("Apple 1kg", 180.0),
            TemplateProduct("Banana 1doz", 60.0)
        ),
        "Grains" to listOf(
            TemplateProduct("Rice 1kg", 65.0),
            TemplateProduct("Wheat Flour 1kg", 45.0)
        ),
        "Dairy" to listOf(
            TemplateProduct("Milk 1L", 60.0),
            TemplateProduct("Eggs 1doz", 80.0)
        )
    )

    val clothingCategories = listOf(
        "T-Shirts" to listOf(
            TemplateProduct("T-Shirt S", 300.0),
            TemplateProduct("T-Shirt M", 300.0),
            TemplateProduct("T-Shirt L", 300.0),
            TemplateProduct("T-Shirt XL", 320.0)
        ),
        "Shirts" to listOf(
            TemplateProduct("Formal Shirt", 600.0),
            TemplateProduct("Casual Shirt", 500.0)
        ),
        "Trousers" to listOf(
            TemplateProduct("Jeans", 900.0),
            TemplateProduct("Formal Pants", 800.0)
        )
    )

    val electronicsCategories = listOf(
        "Chargers" to listOf(
            TemplateProduct("USB Charger", 150.0),
            TemplateProduct("Type-C Cable", 100.0)
        ),
        "Accessories" to listOf(
            TemplateProduct("Earphones", 200.0),
            TemplateProduct("Power Bank", 900.0)
        ),
        "Home" to listOf(
            TemplateProduct("Bulb", 100.0),
            TemplateProduct("Fan", 1500.0)
        )
    )

    val beautyCategories = listOf(
        "Skincare" to listOf(TemplateProduct("Moisturizer", 250.0)),
        "Haircare" to listOf(TemplateProduct("Shampoo", 300.0)),
        "Makeup" to listOf(TemplateProduct("Lipstick", 200.0))
    )

    val servicesCategories = listOf(
        "Services" to listOf(
            TemplateProduct("Consultation", 500.0),
            TemplateProduct("Basic Service", 300.0)
        )
    )

    val generalCategories = listOf(
        "General" to listOf(TemplateProduct("Item 1", 0.0))
    )

    fun categoriesFor(type: String): List<Pair<String, List<TemplateProduct>>> {
        return when (type) {
            "restaurant" -> restaurantCategories
            "grocery" -> groceryCategories
            "clothing" -> clothingCategories
            "electronics" -> electronicsCategories
            "beauty" -> beautyCategories
            "services" -> servicesCategories
            else -> generalCategories
        }
    }

    fun labelFor(type: String): String {
        return when (type) {
            "restaurant" -> "Restaurant / Food"
            "grocery" -> "Grocery"
            "clothing" -> "Clothing"
            "electronics" -> "Electronics"
            "beauty" -> "Beauty"
            "services" -> "Services"
            else -> "General / Other"
        }
    }
}
