UPDATE materiales
SET materiales = 'No Catalogado'
WHERE materiales = '';

UPDATE ventas
SET material = 'No Catalogado'
WHERE material = '';