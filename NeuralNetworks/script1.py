file_name = 'train.txt'
label_name='train_truth.txt'
test_name='test.txt'
import time
import numpy as np
import sklearn.decomposition as deco
from sklearn import svm
from sklearn.metrics import accuracy_score
from sklearn.linear_model import LogisticRegression, LinearRegression
from sklearn.ensemble import RandomForestClassifier
from sklearn import tree

def do_pca(data):
    data = (data - np.mean(data, 0)) / np.std(data, 0) # You need to normalize your data first
    pca = deco.PCA(data.shape[0]) # n_components is the components number after reduction
    data = pca.fit(data).transform(data)
    return data


l=[]
m=[]
n=[]
p=[]
def process():
    training_data_percentage = 80
    testing_data_percentage = 20
    start_time = time.time()

    # Load the file as a numpy array
    data = np.loadtxt(file_name, dtype=str)
    test = np.loadtxt(test_name, dtype=str)
    labels = np.loadtxt(label_name, dtype=str)

    #transforming the datatyoe of the dataset as float
    labels = labels.astype(dtype=float)
    data = data.astype(dtype=float)
    test = test.astype(dtype=float)

    #transposing the test and training data
    data=np.transpose(data)
    test=np.transpose(test)

    #applying pca to both the training and testing data
    # test=do_pca(test)
    # data=do_pca(data)

    #getting the num of examples
    num_of_examples = data.shape[0]

    #calculating the variables for training,testing
    testing_size = round(num_of_examples * float(testing_data_percentage) / 100)
    random_range = range(num_of_examples)
    perm = np.random.permutation(random_range)

    testing_part = data[perm[0:testing_size], :]
    training_part = data[perm[testing_size:], :]

    testing_labels = labels[0:testing_size]
    training_labels = labels[testing_size:]

    #applying svm
    clf = svm.SVC(gamma=0.0001, C=100,kernel='rbf')
    clf.fit(training_part,training_labels)
    predictions=clf.predict(testing_part)

    #applying logistic regression
    logit = LogisticRegression(C=1.0).fit(training_part,training_labels)
    predictions_lr=logit.predict(testing_part)
    #applying Random Forests
    forest = RandomForestClassifier(n_estimators = 100)
    forest = forest.fit(training_part,training_labels)
    predications_rf = forest.predict(testing_part)

    #applying Decision Tree
    clf_tree = tree.DecisionTreeClassifier(max_features='auto')
    clf_tree = clf_tree.fit(training_part,training_labels)
    predications_tr = clf_tree.predict(testing_part)



    #printing the accuracy
    sv_accuracy=accuracy_score(testing_labels,predictions)
    lr_accuracy=accuracy_score(testing_labels,predictions_lr)
    rf_accuracy=accuracy_score(testing_labels,predications_rf)
    tr_accuracy=accuracy_score(testing_labels,predications_tr)
    l.append(rf_accuracy)
    m.append(sv_accuracy)
    n.append(lr_accuracy)
    p.append(tr_accuracy)
    print "SVM accuracy",sv_accuracy
    print "Logistic accuracy",lr_accuracy
    print "Random Forests accuracy",rf_accuracy
    print "Decision Tree accuracy",tr_accuracy

    #predicting the labels using svm
    print "Prediction using SVM:"
    clf = svm.SVC(gamma=0.0001, C=100,kernel='rbf')
    clf.fit(data,labels)
    predictions=clf.predict(test)
    print predictions

    #predicating the labels using logistic regression
    print "Prediction using Logistic Regression:"
    logit = LogisticRegression(C=1.0,penalty = 'l1').fit(data,labels)
    predictions_lr=logit.predict(test)
    print predictions_lr

    #predicating the labels using logistic regression
    print "Prediction using Random forests:"
    forest = RandomForestClassifier(n_estimators = 100)
    forest = forest.fit(data,labels)
    predications_rf = forest.predict(test)
    print predications_rf


     #predicating the labels using logistic regression
    print "Prediction using Decision Trees:"
    clf_tree = RandomForestClassifier(n_estimators = 100)
    clf_tree = clf_tree.fit(data,labels)
    predications_tr = clf_tree.predict(test)
    print predications_tr

if __name__ == '__main__':
    i=0
    while i<10:
        process()
        i+=1
rf_arr=np.asarray(l)
sv_arr=np.asarray(m)
lr_arr=np.asarray(n)
tr_arr=np.asarray(p)

print "Random Forests Accuracy",np.mean(rf_arr)
print "SVM Accuracy",np.mean(sv_arr)
print "Logistic Regression Accuracy",np.mean(lr_arr)
print "Decision Tree Accuracy",np.mean(tr_arr)
