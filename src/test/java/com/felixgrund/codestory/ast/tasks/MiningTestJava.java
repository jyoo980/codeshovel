package com.felixgrund.codestory.ast.tasks;

import com.felixgrund.codestory.ast.execution.MiningExecution;
import com.felixgrund.codestory.ast.services.RepositoryService;
import com.felixgrund.codestory.ast.services.impl.CachingRepositoryService;
import com.felixgrund.codestory.ast.wrappers.Environment;
import com.felixgrund.codestory.ast.util.Utl;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class MiningTestJava {

	private static final String TARGET_FILE_EXTENSION = ".java";
	private static final String TARGET_FILE_PATH = "src/main/java/com/puppycrawl/tools/checkstyle/utils/CommonUtils.java";
//	private static final String TARGET_FILE_PATH = "src/main/java/de/scandio/confluence/plugins/pocketquery/managers/impl/delegates/SqlExternalDatabaseManager.java";
	private static final String TARGET_METHOD = "createPattern";
//	private static final String TARGET_METHOD = "connectViaJdbc";
	private static final String CODESTORY_REPO_DIR = System.getenv("codestory.repo.dir");
	private static final int TARGET_METHOD_STARTLINE = 104;
//	private static final int TARGET_METHOD_STARTLINE = 288;
	private static final String REPO = "checkstyle";
//	private static final String REPO = "pocketquery";
	private static final String START_COMMIT = "119fd4fb33bef9f5c66fc950396669af842c21a3";
//	private static final String START_COMMIT = "79c0ad833fc3b46a3b5248a3e7c826b6d0b513e1"; // master 2018-07-05

	public static void main(String[] args) throws Exception {
		String repositoryPath = CODESTORY_REPO_DIR + "/" + REPO + "/.git";
		Repository repository = Utl.createRepository(repositoryPath);
		Git git = new Git(repository);

		RepositoryService repositoryService = new CachingRepositoryService(git, repository, REPO, repositoryPath);

		RevCommit startCommit = repositoryService.findCommitByName(START_COMMIT);

		Environment env = new Environment(repositoryService);
		env.setFilePath(TARGET_FILE_PATH);
		env.setStartCommitName(START_COMMIT);
		env.setMethodName(TARGET_METHOD);
		env.setStartLine(TARGET_METHOD_STARTLINE);
		env.setFileExtension(TARGET_FILE_EXTENSION);
		env.setStartCommit(startCommit);

		MiningExecution execution = new MiningExecution(env);
		execution.execute();
	}


}
