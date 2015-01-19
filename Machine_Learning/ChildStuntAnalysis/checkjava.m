load('ods.csv');
load('ows.csv');
diffow = abs(ows - TestT(:, 1));
diffod = abs(ods - TestT(:, 2));
max(diffow)
max(diffod)
load('ds.csv');
load('ws.csv');
diffw = abs(ws - yall(:, 1));
diffd = abs(ds - yall(:, 2));
max(diffw)
max(diffd)

