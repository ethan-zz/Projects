clear all;
close all;

markerfile = 'markers.csv';
testfile = 'testc.csv';
splitdatac( 0.7, markerfile, testfile );

[idm1, m1, tm1, idm2, m2, tm2, idf1, f1, tf1, idf2, f2, tf2, M, center, stddev] = composedatac(markerfile, 0, 0, 1);
[Tsidm1, Tsm1, Trtm1, Tsidm2, Tsm2, Trtm2, Tsidf1, Tsf1, Trtf1, Tsidf2, Tsf2, Trtf2, TM, ignore, ignore] = composedatac(testfile, center, stddev, 0);

% COnsider markers within K-times shortest distance
for K = 1: 0.2 : 3;
if K <= 1 
	ym1 = nn1(m1, tm1, Tsm1 ); ym2 = nn1(m2, tm2, Tsm2 );
	yf1 = nn1(f1, tf1, Tsf1 ); yf2 = nn1(f2, tf2, Tsf2 );
else
	ym1 = nnk(m1, tm1, Tsm1, K ); ym2 = nnk(m2, tm2, Tsm2, K );
	yf1 = nnk(f1, tf1, Tsf1, K ); yf2 = nnk(f2, tf2, Tsf2, K );
end
y = []; target = []; ids = [];
[idout yout tout] = consolidate(Tsidm1, ym1, Trtm1); y = [y; yout]; ids = [ids; idout]; target = [target; tout];
[idout yout tout] = consolidate(Tsidm2, ym2, Trtm2); y = [y; yout]; ids = [ids; idout]; target = [target; tout];
[idout yout tout] = consolidate(Tsidf1, yf1, Trtf1); y = [y; yout]; ids = [ids; idout]; target = [target; tout];
[idout yout tout] = consolidate(Tsidf2, yf2, Trtf2); y = [y; yout]; ids = [ids; idout]; target = [target; tout];

K, score = calcscore( y, target, TM )
end
