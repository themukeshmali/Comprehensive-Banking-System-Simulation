# Sample Data Directory

This directory stores serialized banking data files.

## Files
- `customers.dat` — Serialized customer and account data (generated at runtime when you save)
- `sample_transactions.dat` — Sample transaction data (generated at runtime)

## Notes
- Data files are created automatically when you use the "Save Data" option in the application.
- `.dat` files use Java Object Serialization format.
- `.ser` files are excluded from git tracking via `.gitignore`.
