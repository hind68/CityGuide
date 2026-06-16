package com.example.cityguide.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cityguide.models.Experience;
import com.example.cityguide.models.Favorite;
import com.example.cityguide.models.Guide;
import com.example.cityguide.models.Place;
import com.example.cityguide.models.RecentView;
import com.example.cityguide.models.Reservation;
import com.example.cityguide.models.User;
import com.example.cityguide.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cityguide.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "fullName TEXT," +
                "email TEXT UNIQUE," +
                "password TEXT," +
                "phone TEXT)");

        db.execSQL("CREATE TABLE cities (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE)");

        db.execSQL("CREATE TABLE categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE," +
                "icon TEXT," +
                "sortOrder INTEGER)");

        db.execSQL("CREATE TABLE places (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "city TEXT," +
                "category TEXT," +
                "description TEXT," +
                "address TEXT," +
                "latitude REAL," +
                "longitude REAL," +
                "image TEXT," +
                "rating REAL," +
                "phone TEXT)");

        db.execSQL("CREATE TABLE place_images (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "placeId INTEGER," +
                "image TEXT," +
                "isMain INTEGER)");

        db.execSQL("CREATE TABLE guides (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "city TEXT," +
                "languages TEXT," +
                "specialty TEXT," +
                "pricePerHour REAL," +
                "phone TEXT," +
                "email TEXT," +
                "image TEXT," +
                "rating REAL," +
                "description TEXT)");

        db.execSQL("CREATE TABLE experiences (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "city TEXT," +
                "category TEXT," +
                "description TEXT," +
                "duration TEXT," +
                "price REAL," +
                "image TEXT," +
                "rating REAL)");

        db.execSQL("CREATE TABLE favorites (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId INTEGER," +
                "itemId INTEGER," +
                "itemType TEXT)");

        db.execSQL("CREATE TABLE recent_views (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId INTEGER," +
                "itemId INTEGER," +
                "itemType TEXT," +
                "viewedAt INTEGER)");

        db.execSQL("CREATE TABLE reservations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId INTEGER," +
                "guideId INTEGER," +
                "experienceId INTEGER," +
                "date TEXT," +
                "time TEXT," +
                "numberOfHours INTEGER," +
                "totalPrice REAL," +
                "message TEXT," +
                "status TEXT)");

        seedData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS reservations");
        db.execSQL("DROP TABLE IF EXISTS recent_views");
        db.execSQL("DROP TABLE IF EXISTS favorites");
        db.execSQL("DROP TABLE IF EXISTS experiences");
        db.execSQL("DROP TABLE IF EXISTS guides");
        db.execSQL("DROP TABLE IF EXISTS place_images");
        db.execSQL("DROP TABLE IF EXISTS places");
        db.execSQL("DROP TABLE IF EXISTS categories");
        db.execSQL("DROP TABLE IF EXISTS cities");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        createTablesIfMissing(db);
        seedData(db);
    }

    public boolean createUser(String fullName, String email, String password, String phone) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fullName", fullName);
        values.put("email", email);
        values.put("password", password);
        values.put("phone", phone);
        return db.insert("users", null, values) != -1;
    }

    public boolean isEmailExists(String email) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT id FROM users WHERE email = ?",
                new String[]{email}
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public User loginUser(String email, String password) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM users WHERE email = ? AND password = ?",
                new String[]{email, password}
        );
        User user = null;
        if (cursor.moveToFirst()) {
            user = userFromCursor(cursor);
        }
        cursor.close();
        return user;
    }

    public User getUserById(int userId) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM users WHERE id = ?",
                new String[]{String.valueOf(userId)}
        );
        User user = null;
        if (cursor.moveToFirst()) {
            user = userFromCursor(cursor);
        }
        cursor.close();
        return user;
    }

    public boolean updateUserProfile(int userId, String fullName, String phone) {
        ContentValues values = new ContentValues();
        values.put("fullName", fullName);
        values.put("phone", phone);
        return getWritableDatabase().update("users", values, "id = ?",
                new String[]{String.valueOf(userId)}) > 0;
    }

    public List<String> getPlaceCities() {
        List<String> cities = new ArrayList<>();
        cities.add("All Cities");
        Cursor cursor = getReadableDatabase().rawQuery("SELECT name FROM cities ORDER BY name", null);
        while (cursor.moveToNext()) {
            cities.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        }
        cursor.close();
        return cities;
    }

    public List<String> getPlaceCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("All Categories");
        Cursor cursor = getReadableDatabase().rawQuery("SELECT name FROM categories ORDER BY sortOrder, name", null);
        while (cursor.moveToNext()) {
            categories.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        }
        cursor.close();
        return categories;
    }

    public List<Place> getPlaces(String city, String category) {
        List<Place> places = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM places WHERE image IS NOT NULL AND TRIM(image) != ''");
        List<String> args = new ArrayList<>();
        if (city != null && !"All Cities".equals(city)) {
            query.append(" AND city = ?");
            args.add(city);
        }
        if (category != null && !"All Categories".equals(category)) {
            query.append(" AND category = ?");
            args.add(category);
        }
        query.append(" ORDER BY rating DESC");
        Cursor cursor = getReadableDatabase().rawQuery(query.toString(), args.toArray(new String[0]));
        while (cursor.moveToNext()) {
            places.add(placeFromCursor(cursor));
        }
        cursor.close();
        return places;
    }

    public Place getPlaceById(int id) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM places WHERE id = ?",
                new String[]{String.valueOf(id)});
        Place place = null;
        if (cursor.moveToFirst()) {
            place = placeFromCursor(cursor);
        }
        cursor.close();
        return place;
    }

    public List<Guide> getGuides() {
        List<Guide> guides = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM guides ORDER BY rating DESC", null);
        while (cursor.moveToNext()) {
            guides.add(guideFromCursor(cursor));
        }
        cursor.close();
        return guides;
    }

    public Guide getGuideById(int id) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM guides WHERE id = ?",
                new String[]{String.valueOf(id)});
        Guide guide = null;
        if (cursor.moveToFirst()) {
            guide = guideFromCursor(cursor);
        }
        cursor.close();
        return guide;
    }

    public List<Experience> getExperiences() {
        List<Experience> experiences = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM experiences ORDER BY rating DESC", null);
        while (cursor.moveToNext()) {
            experiences.add(experienceFromCursor(cursor));
        }
        cursor.close();
        return experiences;
    }

    public Experience getExperienceById(int id) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM experiences WHERE id = ?",
                new String[]{String.valueOf(id)});
        Experience experience = null;
        if (cursor.moveToFirst()) {
            experience = experienceFromCursor(cursor);
        }
        cursor.close();
        return experience;
    }

    public void addFavorite(int userId, int itemId, String itemType) {
        if (isFavorite(userId, itemId, itemType)) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("itemId", itemId);
        values.put("itemType", itemType);
        getWritableDatabase().insert("favorites", null, values);
    }

    public boolean isFavorite(int userId, int itemId, String itemType) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT id FROM favorites WHERE userId = ? AND itemId = ? AND itemType = ?",
                new String[]{String.valueOf(userId), String.valueOf(itemId), itemType}
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public void removeFavorite(int favoriteId) {
        getWritableDatabase().delete("favorites", "id = ?", new String[]{String.valueOf(favoriteId)});
    }

    public void addRecentView(int userId, int itemId, String itemType) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("recent_views", "userId = ? AND itemId = ? AND itemType = ?",
                new String[]{String.valueOf(userId), String.valueOf(itemId), itemType});
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("itemId", itemId);
        values.put("itemType", itemType);
        values.put("viewedAt", System.currentTimeMillis());
        db.insert("recent_views", null, values);
    }

    public List<RecentView> getRecentViews(int userId, int limit) {
        List<RecentView> recentViews = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM recent_views WHERE userId = ? ORDER BY viewedAt DESC LIMIT ?",
                new String[]{String.valueOf(userId), String.valueOf(limit)}
        );
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow("itemId"));
            String itemType = cursor.getString(cursor.getColumnIndexOrThrow("itemType"));
            RecentView recentView = buildRecentView(id, userId, itemId, itemType);
            if (recentView != null) {
                recentViews.add(recentView);
            }
        }
        cursor.close();
        return recentViews;
    }

    private RecentView buildRecentView(int id, int userId, int itemId, String itemType) {
        if (Constants.FAVORITE_PLACE.equals(itemType)) {
            Place place = getPlaceById(itemId);
            if (place == null) {
                return null;
            }
            return new RecentView(id, userId, itemId, itemType, place.getName(),
                    place.getCity() + " - " + place.getCategory(), place.getImage());
        }
        if (Constants.FAVORITE_GUIDE.equals(itemType)) {
            Guide guide = getGuideById(itemId);
            if (guide == null) {
                return null;
            }
            return new RecentView(id, userId, itemId, itemType, guide.getName(),
                    guide.getCity() + " - " + guide.getSpecialty(), guide.getImage());
        }
        if (Constants.FAVORITE_EXPERIENCE.equals(itemType)) {
            Experience experience = getExperienceById(itemId);
            if (experience == null) {
                return null;
            }
            return new RecentView(id, userId, itemId, itemType, experience.getTitle(),
                    experience.getCity() + " - " + experience.getCategory(), experience.getImage());
        }
        return null;
    }

    public List<Favorite> getFavorites(int userId) {
        List<Favorite> favorites = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM favorites WHERE userId = ? ORDER BY id DESC",
                new String[]{String.valueOf(userId)}
        );
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow("itemId"));
            String itemType = cursor.getString(cursor.getColumnIndexOrThrow("itemType"));
            String title = itemType;
            String subtitle = "Saved item";
            String image = "";
            if (Constants.FAVORITE_PLACE.equals(itemType)) {
                Place place = getPlaceById(itemId);
                if (place != null) {
                    title = place.getName();
                    subtitle = place.getCity() + " - " + place.getCategory();
                    image = place.getImage();
                }
            } else if (Constants.FAVORITE_GUIDE.equals(itemType)) {
                Guide guide = getGuideById(itemId);
                if (guide != null) {
                    title = guide.getName();
                    subtitle = guide.getCity() + " - " + guide.getSpecialty();
                    image = guide.getImage();
                }
            } else if (Constants.FAVORITE_EXPERIENCE.equals(itemType)) {
                Experience experience = getExperienceById(itemId);
                if (experience != null) {
                    title = experience.getTitle();
                    subtitle = experience.getCity() + " - " + experience.getCategory();
                    image = experience.getImage();
                }
            }
            favorites.add(new Favorite(id, userId, itemId, itemType, title, subtitle, image));
        }
        cursor.close();
        return favorites;
    }

    public long addReservation(int userId, int guideId, int experienceId, String date, String time,
                               int numberOfHours, double totalPrice, String message) {
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("guideId", guideId);
        values.put("experienceId", experienceId);
        values.put("date", date);
        values.put("time", time);
        values.put("numberOfHours", numberOfHours);
        values.put("totalPrice", totalPrice);
        values.put("message", message);
        values.put("status", "Pending");
        return getWritableDatabase().insert("reservations", null, values);
    }

    public List<Reservation> getReservations(int userId) {
        List<Reservation> reservations = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM reservations WHERE userId = ? ORDER BY id DESC",
                new String[]{String.valueOf(userId)}
        );
        while (cursor.moveToNext()) {
            reservations.add(reservationFromCursor(cursor));
        }
        cursor.close();
        return reservations;
    }

    public boolean cancelReservation(int reservationId, int userId) {
        ContentValues values = new ContentValues();
        values.put("status", "Cancelled");
        return getWritableDatabase().update("reservations", values, "id = ? AND userId = ?",
                new String[]{String.valueOf(reservationId), String.valueOf(userId)}) > 0;
    }

    private void seedData(SQLiteDatabase db) {
        insertCityIfMissing(db, "Agafay");
        insertCityIfMissing(db, "Casablanca");
        insertCityIfMissing(db, "Chefchaouen");
        insertCityIfMissing(db, "Essaouira");
        insertCityIfMissing(db, "Fes");
        insertCityIfMissing(db, "Marrakech");
        insertCityIfMissing(db, "Rabat");
        insertCityIfMissing(db, "Taghazout");
        insertCityIfMissing(db, "Tangier");

        insertCategoryIfMissing(db, "Restaurant", "ic_restaurant", 1);
        insertCategoryIfMissing(db, "Cafe", "ic_cafe", 2);
        insertCategoryIfMissing(db, "Monument", "ic_monument", 3);
        insertCategoryIfMissing(db, "Activity", "ic_leisure", 4);
        insertCategoryIfMissing(db, "Garden", "ic_leisure", 5);
        insertCategoryIfMissing(db, "Medina", "ic_monument", 6);
        insertCategoryIfMissing(db, "Souk", "ic_restaurant", 7);
        insertCategoryIfMissing(db, "Landmark", "ic_monument", 8);

        insertPlaceWithMainImage(db, "Jardin Majorelle", "Marrakech", "Garden",
                "A peaceful botanical garden known for vivid Majorelle blue, palms and artful paths.",
                "Rue Yves Saint Laurent, Marrakech", 31.6417, -8.0029, "place_majorelle", 4.8, "+212524313047");
        insertPlaceWithMainImage(db, "Jemaa El Fna", "Marrakech", "Monument",
                "The iconic square of storytellers, food stalls, music and evening lights.",
                "Jemaa El Fna, Marrakech", 31.6258, -7.9891, "place_jemaa_el_fna", 4.7, "+212524000000");
        insertPlaceWithMainImage(db, "Hassan Tower", "Rabat", "Monument",
                "A historic minaret and ceremonial plaza facing the Mausoleum of Mohammed V.",
                "Boulevard Mohamed Lyazidi, Rabat", 34.0241, -6.8229, "place_hassan_tower", 4.6, "+212537000000");
        insertPlaceWithMainImage(db, "Oudayas Kasbah", "Rabat", "Monument",
                "A blue-and-white kasbah with ocean views, gardens and quiet alleys.",
                "Kasbah des Oudayas, Rabat", 34.0317, -6.8361, "place_oudayas", 4.7, "+212537000001");
        insertPlaceWithMainImage(db, "Medina of Fes", "Fes", "Medina",
                "A UNESCO-listed maze of artisans, madrasas, markets and ancient streets.",
                "Fes el Bali, Fes", 34.0633, -4.9778, "place_fes_medina", 4.8, "+212535000000");
        insertPlaceWithMainImage(db, "Habous Quarter", "Casablanca", "Souk",
                "A graceful neighborhood of arcades, bookshops, pastry counters and souk stalls.",
                "Quartier Habous, Casablanca", 33.5791, -7.6067, "place_habous", 4.5, "+212522000000");
        insertPlaceWithMainImage(db, "Blue Streets", "Chefchaouen", "Landmark",
                "Photogenic blue lanes tucked into the Rif mountains with calm artisan corners.",
                "Chefchaouen Medina", 35.1688, -5.2636, "place_chefchaouen", 4.9, "+212539000000");

        insertPlaceWithMainImage(db, "Cafe Clock", "Fes", "Cafe",
                "A lively cafe known for storytelling evenings, rooftop views and creative Moroccan plates.",
                "7 Derb El Magana, Fes", 34.0620, -4.9826, "place_cafe_clock_fes", 4.6, "+212535637855");
        insertPlaceWithMainImage(db, "Bacha Coffee", "Marrakech", "Cafe",
                "An elegant coffee house inside Dar El Bacha with refined interiors and a calm courtyard.",
                "Dar El Bacha, Marrakech", 31.6294, -7.9946, "place_bacha_coffee", 4.7, "+212524381611");
        insertPlaceWithMainImage(db, "Cafe Maure", "Rabat", "Cafe",
                "A classic stop near the Oudayas with mint tea, pastries and views over the Bouregreg.",
                "Kasbah des Oudayas, Rabat", 34.0309, -6.8350, "place_cafe_maure", 4.5, "+212537000002");
        insertPlaceWithMainImage(db, "Nomad Marrakech", "Marrakech", "Restaurant",
                "A modern Moroccan rooftop restaurant overlooking the medina and spice square.",
                "Rahba Kedima, Marrakech", 31.6290, -7.9867, "place_nomad_marrakech", 4.5, "+212524381609");
        insertPlaceWithMainImage(db, "La Sqala", "Casablanca", "Restaurant",
                "A garden restaurant in an old fort serving Moroccan classics in a relaxed setting.",
                "Boulevard des Almohades, Casablanca", 33.5981, -7.6171, "place_la_sqala", 4.4, "+212522260960");
        insertPlaceWithMainImage(db, "Dar Zellij", "Marrakech", "Restaurant",
                "A traditional riad restaurant with zellige, lanterns and an elegant dinner atmosphere.",
                "Kaa Sour, Marrakech Medina", 31.6359, -7.9922, "place_dar_zellij", 4.6, "+212524382627");
        insertPlaceWithMainImage(db, "Al Fassia Gueliz", "Marrakech", "Restaurant",
                "A celebrated Moroccan restaurant known for careful service and classic family recipes.",
                "55 Boulevard Zerktouni, Marrakech", 31.6354, -8.0130, "place_al_fassia", 4.6, "+212524434060");
        insertPlaceWithMainImage(db, "Koutoubia Mosque", "Marrakech", "Monument",
                "Marrakech's landmark minaret and gardens, visible from the heart of the city.",
                "Avenue Mohammed V, Marrakech", 31.6240, -7.9931, "place_koutoubia", 4.7, "+212524000002");
        insertPlaceWithMainImage(db, "Bahia Palace", "Marrakech", "Monument",
                "A refined palace of carved cedar, courtyards, painted ceilings and garden rooms.",
                "Rue Riad Zitoun el Jdid, Marrakech", 31.6218, -7.9822, "place_bahia_palace", 4.6, "+212524389564");
        insertPlaceWithMainImage(db, "Hassan II Mosque", "Casablanca", "Monument",
                "A monumental mosque by the Atlantic with intricate craftsmanship and ocean views.",
                "Boulevard de la Corniche, Casablanca", 33.6084, -7.6326, "place_hassan_ii_mosque", 4.8, "+212522482886");
        insertPlaceWithMainImage(db, "Pottery Workshop", "Fes", "Activity",
                "A hands-on artisan stop to learn traditional pottery techniques and painted motifs.",
                "Ain Nokbi, Fes", 34.0572, -4.9998, "place_pottery_workshop_fes", 4.7, "+212535000010");
        insertPlaceWithMainImage(db, "Zellige Workshop", "Fes", "Activity",
                "Meet local makers and discover how Moroccan mosaic tiles are cut and assembled.",
                "Fes Artisan Quarter", 34.0554, -4.9966, "place_zellige_workshop_fes", 4.8, "+212535000011");
        insertPlaceWithMainImage(db, "Agafay Desert Escape", "Agafay", "Activity",
                "A rocky desert experience near Marrakech with sunset views, tea and quiet landscapes.",
                "Agafay Desert", 31.4141, -8.1432, "place_agafay_desert", 4.7, "+212600000020");
        insertPlaceWithMainImage(db, "Gnawa Music Evening", "Essaouira", "Activity",
                "An intimate cultural evening around Gnawa rhythms, local stories and coastal atmosphere.",
                "Essaouira Medina", 31.5085, -9.7595, "place_gnawa_evening", 4.6, "+212524000030");
        insertPlaceWithMainImage(db, "Surf Lesson", "Taghazout", "Activity",
                "A beginner-friendly surf session with local instructors on Morocco's Atlantic coast.",
                "Taghazout Beach", 30.5456, -9.7086, "place_surf_taghazout", 4.7, "+212528000040");

        insertGuideIfMissing(db, "Amina El Fassi", "Fes", "Arabic, French, English", "Medina heritage", 180,
                "+212600111001", "amina.guide@cityguide.ma", "guide_amina", 4.9,
                "A calm heritage guide with deep knowledge of Fes artisans, madrasas and local traditions.");
        insertGuideIfMissing(db, "Youssef Benali", "Marrakech", "Arabic, English, Spanish", "Souks and food", 160,
                "+212600111002", "youssef.guide@cityguide.ma", "guide_youssef", 4.8,
                "A warm Marrakech guide who connects guests with trusted makers, flavors and stories.");
        insertGuideIfMissing(db, "Salma Idrissi", "Rabat", "Arabic, French, English", "History and architecture", 150,
                "+212600111003", "salma.guide@cityguide.ma", "guide_salma", 4.7,
                "A thoughtful cultural host for Rabat's monuments, kasbahs, gardens and ocean views.");
        insertGuideIfMissing(db, "Omar Chafik", "Casablanca", "Arabic, French, English", "Urban culture", 140,
                "+212600111004", "omar.guide@cityguide.ma", "guide_omar", 4.6,
                "A Casablanca local who blends Art Deco history, markets, cafes and modern city life.");
        insertGuideIfMissing(db, "Nadia Amrani", "Chefchaouen", "Arabic, English", "Photography walks", 170,
                "+212600111005", "nadia.guide@cityguide.ma", "guide_nadia", 4.9,
                "A patient visual storyteller for blue streets, mountain light and quiet corners.");
        cleanupGuideImages(db);

        insertExperienceIfMissing(db, "Medina Walk", "Fes", "Walking Tour",
                "A slow walk through historic gates, workshops, fountains and hidden courtyards.",
                "3 hours", 320, "experience_medina_walk", 4.8);
        insertExperienceIfMissing(db, "Moroccan Food Tour", "Marrakech", "Food",
                "Taste msemen, olives, tagine, mint tea and evening square favorites.",
                "4 hours", 450, "experience_food_tour", 4.9);
        insertExperienceIfMissing(db, "Souk Shopping Tour", "Marrakech", "Shopping",
                "Meet artisans and learn how to choose leather, carpets, spices and brass pieces.",
                "3 hours", 300, "experience_souk", 4.7);
        insertExperienceIfMissing(db, "Historical Monuments Tour", "Rabat", "History",
                "Explore towers, kasbahs and royal-era landmarks with clear historical context.",
                "3 hours", 280, "experience_history", 4.6);
        insertExperienceIfMissing(db, "Photography Walk", "Chefchaouen", "Creative",
                "Find soft light, blue alleys and composed viewpoints with a local host.",
                "2 hours", 260, "experience_photo_walk", 4.8);
        insertExperienceIfMissing(db, "Moroccan Cooking Class", "Casablanca", "Cooking",
                "Prepare a market-inspired menu with spices, tea ritual and a shared meal.",
                "4 hours", 520, "experience_cooking", 4.9);

        cleanupLegacyFoodTourData(db);
    }

    private void cleanupGuideImages(SQLiteDatabase db) {
        ContentValues yassineImage = new ContentValues();
        yassineImage.put("image", "guide_yassine");
        db.update("guides", yassineImage, "name LIKE ?", new String[]{"%Yassine%"});
    }

    private void cleanupLegacyFoodTourData(SQLiteDatabase db) {
        ContentValues updates = new ContentValues();
        updates.put("image", "experience_food_tour");
        db.update("experiences", updates, "title LIKE ? OR category = ?",
                new String[]{"%Food Tour%", "Food"});

        int canonicalFoodTourId = getExistingId(db, "experiences", "title = ? AND city = ?",
                new String[]{"Moroccan Food Tour", "Marrakech"});
        if (canonicalFoodTourId > 0) {
            db.delete("experiences", "category = ? AND id != ?",
                    new String[]{"Food", String.valueOf(canonicalFoodTourId)});
        }
        db.delete("experiences", "category IN (?, ?) AND (image IS NULL OR TRIM(image) = '' OR image = ?)",
                new String[]{"Food", "Cooking", "background_splash"});
    }

    private void createTablesIfMissing(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "fullName TEXT," +
                "email TEXT UNIQUE," +
                "password TEXT," +
                "phone TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS cities (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE)");
        db.execSQL("CREATE TABLE IF NOT EXISTS categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE," +
                "icon TEXT," +
                "sortOrder INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS places (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "city TEXT," +
                "category TEXT," +
                "description TEXT," +
                "address TEXT," +
                "latitude REAL," +
                "longitude REAL," +
                "image TEXT," +
                "rating REAL," +
                "phone TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS place_images (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "placeId INTEGER," +
                "image TEXT," +
                "isMain INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS guides (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "city TEXT," +
                "languages TEXT," +
                "specialty TEXT," +
                "pricePerHour REAL," +
                "phone TEXT," +
                "email TEXT," +
                "image TEXT," +
                "rating REAL," +
                "description TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS experiences (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "city TEXT," +
                "category TEXT," +
                "description TEXT," +
                "duration TEXT," +
                "price REAL," +
                "image TEXT," +
                "rating REAL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS favorites (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId INTEGER," +
                "itemId INTEGER," +
                "itemType TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS recent_views (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId INTEGER," +
                "itemId INTEGER," +
                "itemType TEXT," +
                "viewedAt INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS reservations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId INTEGER," +
                "guideId INTEGER," +
                "experienceId INTEGER," +
                "date TEXT," +
                "time TEXT," +
                "numberOfHours INTEGER," +
                "totalPrice REAL," +
                "message TEXT," +
                "status TEXT)");
    }

    private boolean isTableEmpty(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
        boolean empty = true;
        if (cursor.moveToFirst()) {
            empty = cursor.getInt(0) == 0;
        }
        cursor.close();
        return empty;
    }

    private void insertCityIfMissing(SQLiteDatabase db, String name) {
        if (getExistingId(db, "cities", "name = ?", new String[]{name}) > 0) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.insert("cities", null, values);
    }

    private void insertCategoryIfMissing(SQLiteDatabase db, String name, String icon, int sortOrder) {
        if (getExistingId(db, "categories", "name = ?", new String[]{name}) > 0) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("icon", icon);
        values.put("sortOrder", sortOrder);
        db.insert("categories", null, values);
    }

    private void insertPlaceWithMainImage(SQLiteDatabase db, String name, String city, String category,
                                          String description, String address, double latitude, double longitude,
                                          String image, double rating, String phone) {
        int placeId = insertPlaceIfMissing(db, name, city, category, description, address, latitude, longitude,
                image, rating, phone);
        insertPlaceImageIfMissing(db, placeId, image, true);
    }

    private int insertPlaceIfMissing(SQLiteDatabase db, String name, String city, String category,
                                     String description, String address, double latitude, double longitude,
                                     String image, double rating, String phone) {
        int existingId = getExistingId(db, "places", "name = ? AND city = ?", new String[]{name, city});
        if (existingId > 0) {
            ContentValues updates = new ContentValues();
            updates.put("category", category);
            updates.put("description", description);
            updates.put("address", address);
            updates.put("latitude", latitude);
            updates.put("longitude", longitude);
            updates.put("image", image);
            updates.put("rating", rating);
            updates.put("phone", phone);
            db.update("places", updates, "id = ?", new String[]{String.valueOf(existingId)});
            return existingId;
        }
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("city", city);
        values.put("category", category);
        values.put("description", description);
        values.put("address", address);
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        values.put("image", image);
        values.put("rating", rating);
        values.put("phone", phone);
        return (int) db.insert("places", null, values);
    }

    private void insertPlaceImageIfMissing(SQLiteDatabase db, int placeId, String image, boolean isMain) {
        if (placeId <= 0 || image == null) {
            return;
        }
        int existingId = getExistingId(db, "place_images", "placeId = ? AND image = ?",
                new String[]{String.valueOf(placeId), image});
        if (existingId > 0) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put("placeId", placeId);
        values.put("image", image);
        values.put("isMain", isMain ? 1 : 0);
        db.insert("place_images", null, values);
    }

    private void insertGuideIfMissing(SQLiteDatabase db, String name, String city, String languages,
                                      String specialty, double pricePerHour, String phone, String email,
                                      String image, double rating, String description) {
        int existingId = getExistingId(db, "guides", "name = ? AND city = ?", new String[]{name, city});
        if (existingId > 0) {
            ContentValues updates = new ContentValues();
            updates.put("languages", languages);
            updates.put("specialty", specialty);
            updates.put("pricePerHour", pricePerHour);
            updates.put("phone", phone);
            updates.put("email", email);
            updates.put("image", image);
            updates.put("rating", rating);
            updates.put("description", description);
            db.update("guides", updates, "id = ?", new String[]{String.valueOf(existingId)});
            return;
        }
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("city", city);
        values.put("languages", languages);
        values.put("specialty", specialty);
        values.put("pricePerHour", pricePerHour);
        values.put("phone", phone);
        values.put("email", email);
        values.put("image", image);
        values.put("rating", rating);
        values.put("description", description);
        db.insert("guides", null, values);
    }

    private void insertExperienceIfMissing(SQLiteDatabase db, String title, String city, String category,
                                           String description, String duration, double price,
                                           String image, double rating) {
        int existingId = getExistingId(db, "experiences", "title = ? AND city = ?", new String[]{title, city});
        if (existingId > 0) {
            ContentValues updates = new ContentValues();
            updates.put("category", category);
            updates.put("description", description);
            updates.put("duration", duration);
            updates.put("price", price);
            updates.put("image", image);
            updates.put("rating", rating);
            db.update("experiences", updates, "id = ?", new String[]{String.valueOf(existingId)});
            return;
        }
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("city", city);
        values.put("category", category);
        values.put("description", description);
        values.put("duration", duration);
        values.put("price", price);
        values.put("image", image);
        values.put("rating", rating);
        db.insert("experiences", null, values);
    }

    private int getExistingId(SQLiteDatabase db, String table, String whereClause, String[] args) {
        Cursor cursor = db.rawQuery("SELECT id FROM " + table + " WHERE " + whereClause + " LIMIT 1", args);
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        }
        cursor.close();
        return id;
    }

    private User userFromCursor(Cursor cursor) {
        return new User(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("fullName")),
                cursor.getString(cursor.getColumnIndexOrThrow("email")),
                cursor.getString(cursor.getColumnIndexOrThrow("password")),
                cursor.getString(cursor.getColumnIndexOrThrow("phone"))
        );
    }

    private Place placeFromCursor(Cursor cursor) {
        return new Place(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("city")),
                cursor.getString(cursor.getColumnIndexOrThrow("category")),
                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                cursor.getString(cursor.getColumnIndexOrThrow("address")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")),
                cursor.getString(cursor.getColumnIndexOrThrow("image")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
                cursor.getString(cursor.getColumnIndexOrThrow("phone"))
        );
    }

    private Guide guideFromCursor(Cursor cursor) {
        return new Guide(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("city")),
                cursor.getString(cursor.getColumnIndexOrThrow("languages")),
                cursor.getString(cursor.getColumnIndexOrThrow("specialty")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("pricePerHour")),
                cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                cursor.getString(cursor.getColumnIndexOrThrow("email")),
                cursor.getString(cursor.getColumnIndexOrThrow("image")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
                cursor.getString(cursor.getColumnIndexOrThrow("description"))
        );
    }

    private Experience experienceFromCursor(Cursor cursor) {
        return new Experience(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                cursor.getString(cursor.getColumnIndexOrThrow("city")),
                cursor.getString(cursor.getColumnIndexOrThrow("category")),
                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                cursor.getString(cursor.getColumnIndexOrThrow("duration")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                cursor.getString(cursor.getColumnIndexOrThrow("image")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("rating"))
        );
    }

    private Reservation reservationFromCursor(Cursor cursor) {
        int guideId = cursor.getInt(cursor.getColumnIndexOrThrow("guideId"));
        int experienceId = cursor.getInt(cursor.getColumnIndexOrThrow("experienceId"));
        String title = "Reservation";
        if (guideId > 0) {
            Guide guide = getGuideById(guideId);
            if (guide != null) {
                title = guide.getName();
            }
        } else if (experienceId > 0) {
            Experience experience = getExperienceById(experienceId);
            if (experience != null) {
                title = experience.getTitle();
            }
        }
        return new Reservation(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getInt(cursor.getColumnIndexOrThrow("userId")),
                guideId,
                experienceId,
                title,
                cursor.getString(cursor.getColumnIndexOrThrow("date")),
                cursor.getString(cursor.getColumnIndexOrThrow("time")),
                cursor.getInt(cursor.getColumnIndexOrThrow("numberOfHours")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("totalPrice")),
                cursor.getString(cursor.getColumnIndexOrThrow("message")),
                cursor.getString(cursor.getColumnIndexOrThrow("status"))
        );
    }
}
