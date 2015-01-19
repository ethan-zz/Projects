close all;
clear all;
highorder = 0;

trainfile = 'train.csv';
validatefile = 'validate.csv';
testfile = 'test.csv';
splitdata( 0.6, 0.2, trainfile, validatefile, testfile );
[ignore, tm1, trtm1, ignore, tm2, trtm2, ignore, tf1, trtf1, ignore, tf2, trtf2, H] = composedataL(trainfile, highorder);
mus = mean( [tm1;tm2;tf1;tf2] ); sigmas = std( [tm1;tm2;tf1;tf2] );
tm1 = [ones(size(tm1, 1),1) scaledata(tm1, mus, sigmas)]; tm2 = [ones(size(tm2, 1),1) scaledata(tm2, mus, sigmas)];
tf1 = [ones(size(tf1, 1),1) scaledata(tf1, mus, sigmas)]; tf2 = [ones(size(tf2, 1),1) scaledata(tf2, mus, sigmas)];

[ignore, vm1, vrtm1, ignore, vm2, vrtm2, ignore, vf1, vrtf1, ignore, vf2, vrtf2, VH] = composedataL(validatefile, highorder);
vm1 = [ones(size(vm1, 1),1) scaledata(vm1, mus, sigmas)]; vm2 = [ones(size(vm2, 1),1) scaledata(vm2, mus, sigmas)];
vf1 = [ones(size(vf1, 1),1) scaledata(vf1, mus, sigmas)]; vf2 = [ones(size(vf2, 1),1) scaledata(vf2, mus, sigmas)];

[fidm1, fm1, frtm1, fidm2, fm2, frtm2, fidf1, ff1, frtf1, fidf2, ff2, frtf2, TestH] = composedataL(testfile, highorder);
fm1 = [ones(size(fm1, 1),1) scaledata(fm1, mus, sigmas)]; fm2 = [ones(size(fm2, 1),1) scaledata(fm2, mus, sigmas)];
ff1 = [ones(size(ff1, 1),1) scaledata(ff1, mus, sigmas)]; ff2 = [ones(size(ff2, 1),1) scaledata(ff2, mus, sigmas)];

rate = 0.003;
iter = 1000;

lambdaws = [0 0.3 0.7 1 3 7 10 30 70 100 300 700 1000 3000 7000 10000 13000 17000];
lambdads = [0 0.3 0.7 1 3 7 10 30 70 100 300 700 1000 3000 7000 10000 13000 17000];
%lambdaws = [0 0.1 0.3 0.9 1.2];
%lambdads = [0 0.1 0.3 0.9 1.2];

y = []; target = []; ids = [];
[thetaw, thetad] = estimatewd(tm1, trtm1, vm1, vrtm1, iter, rate, lambdaws, lambdads);
yall = fm1 * [thetaw thetad];
[idout yout tout] = consolidate(fidm1, yall, frtm1);
y = [y; yout]; target = [target; tout]; ids = [ids; idout];

[thetaw, thetad] = estimatewd(tm2, trtm2, vm2, vrtm2, iter, rate, lambdaws, lambdads);
yall = fm2 * [thetaw thetad];
[idout yout tout] = consolidate(fidm2, yall, frtm2);
y = [y; yout]; target = [target; tout]; ids = [ids; idout];

[thetaw, thetad] = estimatewd(tf1, trtf1, vf1, vrtf1, iter, rate, lambdaws, lambdads);
yall = ff1 * [thetaw thetad];
[idout yout tout] = consolidate(fidf1, yall, frtf1);
y = [y; yout]; target = [target; tout]; ids = [ids; idout];

[thetaw, thetad] = estimatewd(tf2, trtf2, vf2, vrtf2, iter, rate, lambdaws, lambdads);
yall = ff2 * [thetaw thetad];
[idout yout tout] = consolidate(fidf2, yall, frtf2);
y = [y; yout]; target = [target; tout]; ids = [ids; idout];

score = calcscore( y, target, TestH )



