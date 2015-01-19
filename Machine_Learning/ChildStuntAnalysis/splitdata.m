function splitdata(trainportion, validateportion, traincsv, validatecsv, testcsv)

raw = load('data.csv');
[ids, ia, ic] = unique( raw(:, 1) );
% check data on how many entried each ID has
bincount = histc( raw(:,1), ids );
%minsize = min( bincount)
%maxsize = max( bincount )
%minsize_ids = ids( find( bincount == minsize ) );
%maxsize_ids = ids( find( bincount == maxsize ) );

% Split data train-validate-test
iatrain = ia(1:ceil( length(ia) * trainportion));
iavalidate = ia( length(iatrain) + 1: length(iatrain) + ceil( length(ia) * validateportion) );
iatest = ia( length(iatrain) + length(iavalidate) + 1: end);

ictrainend = find( ic == length(iatrain) + 1, 1) - 1;
ictrain = ic(1:ictrainend);
icvalidateend = find( ic == length(iatrain) + length(iavalidate) + 1, 1) - 1;
icvalidate = ic(ictrainend + 1 : icvalidateend) - length(iatrain);
ictest = ic(icvalidateend + 1 : end) - length(iatrain) - length(iavalidate);
testIdx = icvalidateend + 1;

csvwrite(traincsv, raw( 1:ictrainend, :) );
csvwrite(validatecsv, raw( (ictrainend + 1):icvalidateend, :) );
csvwrite(testcsv, raw( (icvalidateend + 1):size(raw,1), :) );

% for Java code to use
csvwrite('javatrain.csv', raw( 1:icvalidateend, :) );
end


