// docker/mongodb/init.js
// Runs automatically on first container start

db = db.getSiblingDB('productdb');

// Create a dedicated app user
db.createUser({
  user: 'appuser',
  pwd: 'appuser123',
  roles: [{ role: 'readWrite', db: 'productdb' }]
});

// Create the products collection with basic validation
db.createCollection('products', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['code', 'name', 'category', 'price', 'quantity'],
      properties: {
        code:     { bsonType: 'string' },
        name:     { bsonType: 'string' },
        category: { bsonType: 'string' },
        price:    { bsonType: 'double' },
        quantity: { bsonType: 'int'    }
      }
    }
  }
});

// Index on code for fast lookups
db.products.createIndex({ code: 1 }, { unique: true });

print('MongoDB initialized — productdb ready');
