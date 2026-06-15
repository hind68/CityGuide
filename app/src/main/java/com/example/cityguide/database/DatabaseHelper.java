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
        db.execSQL("DROP TABLE IF EXISTS favorites");
        db.execSQL("DROP TABLE IF EXISTS experiences");
        db.execSQL("DROP TABLE IF EXISTS guides");
        db.execSQL("DROP TABLE IF EXISTS places");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        createTablesIfMissing(db);
        if (isTableEmpty(db, "places")) {
            seedData(db);
        }
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

    public List<Place> getPlaces(String city, String category) {
        List<Place> places = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM places WHERE 1=1");
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
            if (Constants.FAVORITE_PLACE.equals(itemType)) {
                Place place = getPlaceById(itemId);
                if (place != null) {
                    title = place.getName();
                    subtitle = place.getCity() + " · " + place.getCategory();
                }
            } else if (Constants.FAVORITE_GUIDE.equals(itemType)) {
                Guide guide = getGuideById(itemId);
                if (guide != null) {
                    title = guide.getName();
                    subtitle = guide.getCity() + " · " + guide.getSpecialty();
                }
            } else if (Constants.FAVORITE_EXPERIENCE.equals(itemType)) {
                Experience experience = getExperienceById(itemId);
                if (experience != null) {
                    title = experience.getTitle();
                    subtitle = experience.getCity() + " · " + experience.getCategory();
                }
            }
            favorites.add(new Favorite(id, userId, itemId, itemType, title, subtitle));
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

    private void seedData(SQLiteDatabase db) {
        insertPlace(db, "Jardin Majorelle", "Marrakech", "Garden",
                "A peaceful botanical garden known for vivid Majorelle blue, palms and artful paths.",
                "Rue Yves Saint Laurent, Marrakech", 31.6417, -8.0029, "place_majorelle", 4.8, "+212524313047");
        insertPlace(db, "Jemaa El Fna", "Marrakech", "Monument",
                "The iconic square of storytellers, food stalls, music and evening lights.",
                "Jemaa El Fna, Marrakech", 31.6258, -7.9891, "place_jemaa_el_fna", 4.7, "+212524000000");
        insertPlace(db, "Hassan Tower", "Rabat", "Monument",
                "A historic minaret and ceremonial plaza facing the Mausoleum of Mohammed V.",
                "Boulevard Mohamed Lyazidi, Rabat", 34.0241, -6.8229, "place_hassan_tower", 4.6, "+212537000000");
        insertPlace(db, "Oudayas Kasbah", "Rabat", "Monument",
                "A blue-and-white kasbah with ocean views, gardens and quiet alleys.",
                "Kasbah des Oudayas, Rabat", 34.0317, -6.8361, "place_oudayas", 4.7, "+212537000001");
        insertPlace(db, "Medina of Fes", "Fes", "Medina",
                "A UNESCO-listed maze of artisans, madrasas, markets and ancient streets.",
                "Fes el Bali, Fes", 34.0633, -4.9778, "place_fes_medina", 4.8, "+212535000000");
        insertPlace(db, "Habous Quarter", "Casablanca", "Souk",
                "A graceful neighborhood of arcades, bookshops, pastry counters and souk stalls.",
                "Quartier Habous, Casablanca", 33.5791, -7.6067, "place_habous", 4.5, "+212522000000");
        insertPlace(db, "Blue Streets", "Chefchaouen", "Landmark",
                "Photogenic blue lanes tucked into the Rif mountains with calm artisan corners.",
                "Chefchaouen Medina", 35.1688, -5.2636, "place_chefchaouen", 4.9, "+212539000000");

        insertGuide(db, "Amina El Fassi", "Fes", "Arabic, French, English", "Medina heritage", 180,
                "+212600111001", "amina.guide@cityguide.ma", "guide_amina", 4.9,
                "A calm heritage guide with deep knowledge of Fes artisans, madrasas and local traditions.");
        insertGuide(db, "Youssef Benali", "Marrakech", "Arabic, English, Spanish", "Souks and food", 160,
                "+212600111002", "youssef.guide@cityguide.ma", "guide_youssef", 4.8,
                "A warm Marrakech guide who connects guests with trusted makers, flavors and stories.");
        insertGuide(db, "Salma Idrissi", "Rabat", "Arabic, French, English", "History and architecture", 150,
                "+212600111003", "salma.guide@cityguide.ma", "guide_salma", 4.7,
                "A thoughtful cultural host for Rabat's monuments, kasbahs, gardens and ocean views.");
        insertGuide(db, "Omar Chafik", "Casablanca", "Arabic, French, English", "Urban culture", 140,
                "+212600111004", "omar.guide@cityguide.ma", "guide_omar", 4.6,
                "A Casablanca local who blends Art Deco history, markets, cafes and modern city life.");
        insertGuide(db, "Nadia Amrani", "Chefchaouen", "Arabic, English", "Photography walks", 170,
                "+212600111005", "nadia.guide@cityguide.ma", "guide_nadia", 4.9,
                "A patient visual storyteller for blue streets, mountain light and quiet corners.");

        insertExperience(db, "Medina Walk", "Fes", "Walking Tour",
                "A slow walk through historic gates, workshops, fountains and hidden courtyards.",
                "3 hours", 320, "experience_medina_walk", 4.8);
        insertExperience(db, "Moroccan Food Tour", "Marrakech", "Food",
                "Taste msemen, olives, tagine, mint tea and evening square favorites.",
                "4 hours", 450, "experience_food_tour", 4.9);
        insertExperience(db, "Souk Shopping Tour", "Marrakech", "Shopping",
                "Meet artisans and learn how to choose leather, carpets, spices and brass pieces.",
                "3 hours", 300, "experience_souk", 4.7);
        insertExperience(db, "Historical Monuments Tour", "Rabat", "History",
                "Explore towers, kasbahs and royal-era landmarks with clear historical context.",
                "3 hours", 280, "experience_history", 4.6);
        insertExperience(db, "Photography Walk", "Chefchaouen", "Creative",
                "Find soft light, blue alleys and composed viewpoints with a local host.",
                "2 hours", 260, "experience_photo_walk", 4.8);
        insertExperience(db, "Moroccan Cooking Class", "Casablanca", "Cooking",
                "Prepare a market-inspired menu with spices, tea ritual and a shared meal.",
                "4 hours", 520, "experience_cooking", 4.9);
    }

    private void createTablesIfMissing(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "fullName TEXT," +
                "email TEXT UNIQUE," +
                "password TEXT," +
                "phone TEXT)");
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

    private void insertPlace(SQLiteDatabase db, String name, String city, String category,
                             String description, String address, double latitude, double longitude,
                             String image, double rating, String phone) {
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
        db.insert("places", null, values);
    }

    private void insertGuide(SQLiteDatabase db, String name, String city, String languages,
                             String specialty, double pricePerHour, String phone, String email,
                             String image, double rating, String description) {
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

    private void insertExperience(SQLiteDatabase db, String title, String city, String category,
                                  String description, String duration, double price,
                                  String image, double rating) {
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
