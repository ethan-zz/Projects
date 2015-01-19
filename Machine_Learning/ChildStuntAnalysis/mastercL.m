clear all;
close all;

markerfile = 'markers.csv';
testfile = 'testc.csv';
splitdatac( 0.8, markerfile, testfile );

[idm1, m1, tm1, idm2, m2, tm2, idf1, f1, tf1, idf2, f2, tf2, M, center, stddev] = composedatacL(markerfile, 0, 0, 1);
[Tsidm1, Tsm1, Trtm1, Tsidm2, Tsm2, Trtm2, Tsidf1, Tsf1, Trtf1, Tsidf2, Tsf2, Trtf2, TM, ignore, ignore] = composedatacL(testfile, center, stddev, 0);

% COnsider markers within K-times shortest distance
K = 3
if K <= 1
	ym1 = nn1(m1, tm1, Tsm1 ); ym2 = nn1(m2, tm2, Tsm2 );
	yf1 = nn1(f1, tf1, Tsf1 ); yf2 = nn1(f2, tf2, Tsf2 );
else
	ym1 = nnk(m1, tm1, Tsm1, K ); ym2 = nnk(m2, tm2, Tsm2, K );
	yf1 = nnk(f1, tf1, Tsf1, K ); yf2 = nnk(f2, tf2, Tsf2, K );
end
y = []; target = []; ids = [];
y = [y; ym1]; ids = [ids; Tsidm1]; target = [target; Trtm1];
y = [y; ym2]; ids = [ids; Tsidm2]; target = [target; Trtm2];
y = [y; yf1]; ids = [ids; Tsidf1]; target = [target; Trtf1];
y = [y; yf2]; ids = [ids; Tsidf2]; target = [target; Trtf2];

score = calcscore( y, target, TM )

