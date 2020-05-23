%specify number of runs of simulation
k = 1;

C_data = cell(k,1);
SortedC_data = cell(k,1);
WaitCons_data = cell(k,1);
WaitCorp_data = cell(k,1);
for i = 1:k
i = i-1;
%retrieve the call data
%CHANGE THE FILEPATH TO THE DESIRED FILEPATH OF ALL RELEVANT CSV FILES
fileN = ['/Users/HendrikS/Documents/GitHub/Telco_Simulation/informationCalls',num2str(i),'.csv'];
C_data{i+1} = readtable(fileN);
C_data{i+1}.Properties.VariableNames = {'cstm_tp' 'CSA_tp' 'tme_incoming' 'tme_start' 'tme_end'};

% sort C_data according to tme_incoming
SortedC_data{i+1} = sortrows(C_data{i+1},3);

%retrieve the waiting times of consumer costumers
%CHANGE THE FILEPATH TO THE DESIRED FILEPATH OF ALL RELEVANT CSV FILES
fileWaitCons = ['/Users/HendrikS/Documents/GitHub/Telco_Simulation/waitingTimesConsumer',num2str(i),'.csv'];
WaitCons_data{i+1} = readtable(fileWaitCons);
WaitCons_data{i+1}.Properties.VariableNames = {'consumer_wait_tme'};

%retrieve the waiting times of corporate costumers
%CHANGE THE FILEPATH TO THE DESIRED FILEPATH OF ALL RELEVANT CSV FILES
fileWaitCorp = ['/Users/HendrikS/Documents/GitHub/Telco_Simulation/waitingTimesCorporate',num2str(i),'.csv'];
WaitCorp_data{i+1} = readtable(fileWaitCorp);
WaitCorp_data{i+1}.Properties.VariableNames = {'corporate_wait_tme'};
end


%% Validation of Simulation

%specify amount of runs we want to use for distribution validation
repVal = 1;

consData = cell(repVal,1);
corpData = cell(repVal,1);
serviceTmeCons = cell(repVal,1);
serviceTmeCorp = cell(repVal,1);

for i = 1:repVal
%get all data from consumer calls from each sim. run
ConsTbl = (SortedC_data{i}.cstm_tp ==0);
consData{i} = SortedC_data{i}(ConsTbl,:);

%get all data from corporate calls from each sim. run
CorpTbl = (SortedC_data{i}.cstm_tp ==1);
corpData{i} = SortedC_data{i}(CorpTbl,:);

%get the service times of consumers
serviceTmeCons{i} = consData{i}{:,5}-consData{i}{:,4};
d1 = ['Shortest consumer call service time in ',num2str(i), '. run: ']; 
d2 = ['Longest consumer call service time in ',num2str(i), '. run: ' ];
disp(d1)
%see next section                                                    !!
disp(min(serviceTmeCons{i}))
disp(d2)
%highest values are not truncated
disp(max(serviceTmeCons{i}))

%get the service times of corporates
serviceTmeCorp{i} = corpData{i}{:,5}-corpData{i}{:,4};
d3 = ['Shortest corporate call service time in ',num2str(i), '. run: ']; 
d4 = ['Longest corporate call service time in ',num2str(i), '. run: ' ];
disp(d3)
%smallest values are >45 as desired
disp(min(serviceTmeCorp{i}))
disp(d4)
%highest values are not truncated
disp(max(serviceTmeCorp{i}))

%get the interarrival tmes of consumers
%Simulation starts at 21600

interTmeCons{i}(1) =  (consData{i}{1:1,{'tme_incoming'}}-21600);
for j = 1:(height(consData{i})-1)  
interTmeCons{i}(j+1) = (consData{i}{j+1:j+1,{'tme_incoming'}}-consData{i}{j:j,{'tme_incoming'}});
end

%get the interarrival tmes of corporates

interTmeCorp{i}(1) =  (corpData{i}{1:1,{'tme_incoming'}}-21600);

for j = 1:(height(corpData{i})-1)  
interTmeCorp{i}(j+1) = (corpData{i}{j+1:j+1,{'tme_incoming'}}-corpData{i}{j:j,{'tme_incoming'}});
end

end

%%
%print the data
%consData{1}
%corpData{1}
%______________
%embedd this in loop of previous section if wish to plot more runs
%here only the data from the first run is used
%plot a distribution histogram, superimposing normal dist. for
%consumer and corporate service times
figure(1)
histfit(serviceTmeCons{1})
ylabel('amount of occurrence');
xlabel('service time in sec');
title('Consumer data: Fitting normal to observed service tmes');
figure(2)
histfit(serviceTmeCorp{1})
ylabel('amount of occurrence');
xlabel('service time in sec');
title('Corporate data: Fitting normal to observed service tmes');

%plot a distribution histogram, superimposing exponential dist. for
%consumer and corporate interarrival times
figure(3)
histfit(interTmeCons{1},[],'exponential')
ylabel('amount of occurrence');
xlabel('interarrival time in sec');
title('Consumer data: Fitting exponential to observed interarrvial tmes(s)');
figure(4)
histfit(interTmeCorp{1},[],'exponential')
ylabel('amount of occurrence');
xlabel('interarrival time in sec');
title('Corporate data: Fitting exponential to observed interarrvial tmes(s)');




 







