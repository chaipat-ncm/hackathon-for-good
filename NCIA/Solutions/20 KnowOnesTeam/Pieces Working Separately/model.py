import pandas as pd
import sklearn

def count_labels():
    fo = open("labelz-counted.txt", "w")
    with open("labelz.txt") as fi:
        line = fi.readline()
        counter = 0
        previous_line = ""
        while line != "":
            line = line.strip()
            if line == previous_line:
                counter += 1
            else:
                fo.write("{},{}\n".format(previous_line, counter))
                counter = 1
            previous_line = line
            line = fi.readline()
    fo.close()

def make_columns():
    columns = []
    with open("uni.txt") as fi:
        line = fi.readline()
        while line != "":
            columns.append(line.strip())
            line = fi.readline()
    #print(columns)


    columns = sorted(columns)
    dict = {}
    fo = open("data.txt", "w")
    column_names = ",".join(columns)
    fo.write("id,{},y\n".format(column_names))
    with open("all_labels.txt") as fi:
        line = fi.readline()
        previous_index = ""
        previous_y = ""
        temp_columns = columns[:]
        # FOR EVERY LINE UNTIL END OF FILE
        while line != "":
            line = line.strip()
            line_parts = line.split(',')

            current_index = line_parts[0]
            label = line_parts[4]

            if current_index == previous_index or previous_index == '':
                # remove label from temp labels
                # make label be 1 in a dict
                dict[label] = 1
                try:
                    temp_columns.remove(label)
                except:
                    pass
            else: # WHEN NEW INDEX
                # fill the rest of the dict labels with 0s                
                for column in temp_columns:
                    dict[column] = 0                
                #reinitialize temp_columns
                temp_columns = columns[:]
                
                id = previous_index
                features = ",".join([str(dict[col]) for col in columns])
                y = previous_y
                
                fo.write("{},{},{}\n".format(id, features, y))
                dict = {}
            previous_index = current_index
            previous_y = line_parts[2]
            line = fi.readline()
    fo.close()

def make_model():
    data = pd.read_csv("data.txt")
    
    print(data.sum(axis=0, numeric_only=True).to_csv('somthing.csv'))
    label_counts = pd.read_csv("somthing.csv", header=1, names=['label', 'count'])
#    print data.describe()
    print(label_counts.head())
    print(label_counts.query('count>5').head())
    print(label_counts.query('count>5').count())
    print(label_counts.query('count>5'))
    label_counts.query('count>5').label.to_csv('selection.csv', index=False)

    with open("selection.csv") as fi:
        selected_column = fi.readline()
        index=0
        line = fi.readline()
        while line != "":
            line = line.strip()
            
            with open("columns/{}-{}.csv".format(index, line), 'a') as out:
                out.write("{}\n".format(line))
                data[line].to_csv(out, index=False)
            
            index += 1
            line = fi.readline()

def main():
    # data = pd.read_csv("all_labels.txt", header=0, names=['indexorsmth', 'filename', 'ground_truth', 'labelindex', 'label'])
    # print(data.head())
    # print data.pivot(index=None, )#, 'filename', 'ground_truth'])
    
    
    # RUN ONCE:
    #count_labels()
    #make_columns()

    # MODEL
    make_model()

    


if __name__ == "__main__":
    main()