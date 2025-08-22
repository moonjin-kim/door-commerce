EXPLAIN
SELECT * FROM product
order by like_count DESC
LIMIT 10000 OFFSET 30;


like_count_index
{
    "id": 1,
    "select_type": "SIMPLE",
    "table": "product",
    "partitions": null,
    "type": "index",
    "possible_keys": null,
    "key": "product_like_count_index",
    "key_len": "9",
    "ref": null,
    "rows": 10030,
    "filtered": 100,
    "Extra": "Backward index scan"
}


SELECT * FROM product
where brand_id = 339
LIMIT 10000 OFFSET 30;

brand_id_index
{
"id": 1,
"select_type": "SIMPLE",
"table": "product",
"partitions": null,
"type": "ref",
"possible_keys": "product_brand_id_index",
"key": "product_brand_id_index",
"key_len": "9",
"ref": "const",
"rows": 1045,
"filtered": 100,
"Extra": null
}

EXPLAIN
SELECT * FROM product
order by price ASC
LIMIT 10000 OFFSET 30;

price_index
{
    "id": 1,
    "select_type": "SIMPLE",
    "table": "product",
    "partitions": null,
    "type": "index",
    "possible_keys": null,
    "key": "product_price_index",
    "key_len": "18",
    "ref": null,
    "rows": 10030,
    "filtered": 100,
    "Extra": null
}
