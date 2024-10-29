package com.example.git_branches_creator

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import git4idea.branch.GitBrancher
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler
import git4idea.repo.GitRepositoryManager

class CreateBranchesAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val repositories = GitRepositoryManager.getInstance(project).repositories

        if (repositories.isEmpty()) {
            println("Not GIT repositories found")
            return
        }

        val repoWithChanges = repositories.filter { repo ->
            val git = Git.getInstance()
            val result = git.runCommand(
                GitLineHandler(project, repo.root, GitCommand.STATUS).apply {
                    addParameters("--porcelain")
                }
            )
            result.output.isNotEmpty()
        }

        val branchName = Messages.showInputDialog(
            "Enter new branch name",
            "Create New Branches",
            Messages.getQuestionIcon()
        )

        if (branchName.isNullOrBlank()) {
            println("Branch name is empty")
            return
        }

        GitBrancher.getInstance(project).apply {
            checkoutNewBranch(branchName, repoWithChanges)
        }
    }
}