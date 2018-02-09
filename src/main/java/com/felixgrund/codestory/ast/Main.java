package com.felixgrund.codestory.ast;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.shaded.org.objenesis.strategy.StdInstantiatorStrategy;
import com.felixgrund.codestory.ast.entities.CommitInfo;
import com.felixgrund.codestory.ast.entities.CommitInfoCollection;
import com.felixgrund.codestory.ast.entities.DiffInfo;
import com.felixgrund.codestory.ast.interpreters.Interpreter;
import com.felixgrund.codestory.ast.tasks.CreateCommitInfoCollectionTask;
import com.felixgrund.codestory.ast.util.Utl;
import com.thoughtworks.xstream.XStream;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

public class Main {

	private static final String PROJECT_DIR = System.getProperty("user.dir");

	public static void main(String[] args) {
		try {
			execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void execute() throws Exception {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File("/Users/felix/dev/projects/jquery/.git"))
				.readEnvironment() // scan environment GIT_* variables
				.findGitDir() // scan up the file system tree
				.build();
		Git git = new Git(repository);

		CreateCommitInfoCollectionTask task = new CreateCommitInfoCollectionTask(repository);
		task.setBranchName("master");
		task.setFilePath("src/data.js");
		task.setFileName("data.js");
		task.setFunctionName("data");
		task.setFunctionStartLine(97);
		task.setStartCommitName("294a3698811d6aaeabc67d2a77a5ef5fac94165a");

		task.run();

		for (CommitInfo commitInfo : task.getResult()) {
			Interpreter interpreter = new Interpreter(commitInfo);
			interpreter.interpret();

			if (!interpreter.getFindings().isEmpty()) {
				System.out.println("\n"+commitInfo);
				System.out.println(interpreter.getFindings());
			}

		}

	}


}