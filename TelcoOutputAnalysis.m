%specify number of runs of simulation
k = 1;

C_data = cell(k,1);
WaitCons_data = cell(k,1);
WaitCorp_data = cell(k,1);
for i = 1:k
i = i-1;
%retrieve the call data
%CHANGE THE FILEPATH TO THE DESIRED FILEPATH OF ALL RELEVANT CSV FILES
fileN = ['/Users/HendrikS/Documents/GitHub/Telco_Simulation/informationCalls',num2str(i),'.csv'];
C_data{i+1} = readtable(fileN)
C_data{i+1}.Properties.VariableNames = {'cstm_tp' 'CSA_tp' 'tme_incoming' 'tme_start' 'tme_end'};

%retrieve the waiting times of consumer costumers
%CHANGE THE FILEPATH TO THE DESIRED FILEPATH OF ALL RELEVANT CSV FILES
fileWaitCons = ['/Users/HendrikS/Documents/GitHub/Telco_Simulation/waitingTimesConsumer',num2str(i),'.csv'];
WaitCons_data{i+1} = readtable(fileWaitCons)
WaitCons_data{i+1}.Properties.VariableNames = {'consumer_wait_tme'};

%retrieve the waiting times of corporate costumers
%CHANGE THE FILEPATH TO THE DESIRED FILEPATH OF ALL RELEVANT CSV FILES
fileWaitCorp = ['/Users/HendrikS/Documents/GitHub/Telco_Simulation/waitingTimesCorporate',num2str(i),'.csv'];
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
ConsTbl = (C_data{i}.cstm_tp ==0);
consData{i} = C_data{i}(ConsTbl,:);
%get the service times of consumers
serviceTmeCons{i} = consData{i}{:,5}-consData{i}{:,4};
d1 = ['Shortest consumer call service time in ',num2str(i), '. run: ']; 
d2 = ['Longest consumer call service time in ',num2str(i), '. run: ' ];
disp(d1)
disp(min(serviceTmeCons{i}))
disp(d2)
disp(max(serviceTmeCons{i}))
%get all data from corporate calls from each sim. run
CorpTbl = (C_data{i}.cstm_tp ==1);
corpData{i} = C_data{i}(CorpTbl,:);
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
end

%print the data
%consData{1}
%corpData{1}

figure(1)
histfit(serviceTmeCons{1})
%plot a distribution histogram, superimposing normal for
%corporate service times
figure(2)
histfit(serviceTmeCorp{1})

%% 
% finding bug
irr_stmCons= (serviceTmeCons{1}<25)

S_cons = sort(serviceTmeCons{1});

S_cons(1:505)
length(serviceTmeCons{1}(irr_stmCons))

irregularEntries = ((consData{1}.tme_end - consData{1}.tme_start)<25);

consData{1}(irregularEntries,:)

%serviceTmeCons{1}
%consData{1}((consData{1}{:,5}-consData{1}{:,4})<25)



%corpData{1}(1:5,{'tme_start','tme_end'})





%largest value can in theory be arbitrarily large:










%22/05/20











