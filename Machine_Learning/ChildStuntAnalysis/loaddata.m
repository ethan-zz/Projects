function [train, trainT, trainM, validate, validateT, validateM, test, testT, testM] = loaddata(usehighorder)

trainfile = 'train.csv';
validatefile = 'validate.csv';
testfile = 'test.csv';
splitdata( 0.6, 0.2, trainfile, validatefile, testfile );

[train, trainT, trainM] = composedata( trainfile, usehighorder );
[validate, validateT,validateM] =  composedata( validatefile, usehighorder );
[test, testT, testM] =  composedata( testfile, usehighorder );

end


