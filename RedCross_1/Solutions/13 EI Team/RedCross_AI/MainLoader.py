import ImgLoader
import DataContainer

data_dir = '/home/stephan/Documents/Data_01/RedCross/'
output_dir = data_dir + 'tfrecords'
input_file = data_dir + '/damages.csv'
input_dir = data_dir + 'after_houses'
bef_input_dir = data_dir + 'after_houses'

ImgLoader = ImgLoader.ImgLoader(output_dir, input_file, input_dir, bef_input_dir)

ImgLoader.create_image_records()

# ImgLoader.rec_img(output_dir + '/sub_dev_record')

# dc = DataContainer.DataContainer(output_dir + '/sub_train_record', output_dir + "/sub_dev_record")
#
# dc.visualise_data()