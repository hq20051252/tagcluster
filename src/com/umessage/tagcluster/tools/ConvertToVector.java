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
		NamedVector wordvec;
		
		if (args.length != 2) {
			for (int i =0; i < args.length; i++) {
				System.out.println("The " + new Integer(i).toString() + " argument: " + args[i]);
			}
			Usage();
			System.exit(1);
		}
		String input = args[0];
		String output = args[1];
		
		// 输入文件
		File f = new File(input);
		
		FileInputStream fis = new FileInputStream(f);
		InputStreamReader dis = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(dis);
		
		// 输出文件
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		Path path = new Path(output);
		SequenceFile.Writer writer = new SequenceFile.Writer(fs,  conf,  path, Text.class,  VectorWritable.class);
		
		VectorWritable vec = new VectorWritable();
		
		// 内容处理,转换成vector.
		int pregress = 0; // 进度指示器
		String line;
		while ((line = br.readLine()) != null){
			pregress ++;
			// 打印进度
			System.out.print("Progress: " + new Integer(pregress).toString() + ".\r");
			String[] ss = line.trim().split(" ", -1);
			if (ss.length != 101){
				continue;
			}
			String key = ss[0];
			double[] value = new double[100];
			
			for (int i = 1; i <= 100; i++) {
				value[i-1] = new Double(ss[i]);
			}
			wordvec = new NamedVector(new DenseVector(value), key);
			vec.set(wordvec);
			writer.append(new Text(wordvec.getName()), vec);
		}
		br.close();
		writer.close();
	}

	private static void Usage() {
		System.out.println("Usage: ConvertToVector <input> <output>.");
		return;
	}
}