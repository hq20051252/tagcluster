package com.umessage.tagcluster.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.VectorWritable;

public class ConvertToVector {

	public static void main(String[] args) throws Exception{
		List<NamedVector> wordvecs = new ArrayList<NamedVector>();
		
		NamedVector wordvec;
		
		if (args.length != 3) {
			Usage();
			System.exit(1);
		}
		String input = args[1];
		String output = args[2];
		
		File f = new File(input);
		
		FileInputStream fis = new FileInputStream(f);
		InputStreamReader dis = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(dis);
		
		// 内容处理,转换成vector.
		String line;
		while ((line = br.readLine()) != null){
			String[] ss = line.split(" ", -1);
			if (ss.length != 101){
				continue;
			}
			String key = ss[0];
			double[] value = new double[101];
			
			for (int i = 1; i <= 100; i++) {
				value[i] = new Double(ss[i]);
			}
			wordvec = new NamedVector(new DenseVector(value), key);
			wordvecs.add(wordvec);
		}
		br.close();
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		Path path = new Path(output);
		SequenceFile.Writer writer = new SequenceFile.Writer(fs,  conf,  path, Text.class,  VectorWritable.class);
		
		VectorWritable vec = new VectorWritable();
		for (NamedVector vector : wordvecs) {
			vec.set(vector);
			writer.append(new Text(vector.getName()), vec);
		}
		writer.close();
	}

	private static void Usage() {
		System.out.println("Usage: ConvertToVector <input> <output>.");
		return;
	}
}
