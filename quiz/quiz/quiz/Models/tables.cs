using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;
using quiz.Data;
using System;
using System.ComponentModel.DataAnnotations;

namespace quiz.Models
{
    // Consolidated context for Identity and custom tables
    public class Tablecontext : ApplicationDbContext
    {
        public Tablecontext(DbContextOptions<ApplicationDbContext> options) : base(options)
        {
        }


        // Custom entities
        public DbSet<Quiz> Quiz { get; set; }
        public DbSet<Question> Questions { get; set; }
        public DbSet<Results> Results { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder); // Apply Identity configurations

            // Quiz table configuration
            modelBuilder.Entity<Quiz>().ToTable("Quiz");
            modelBuilder.Entity<Quiz>().HasKey(q => q.Id);

            // Question table configuration
            modelBuilder.Entity<Question>().ToTable("Questions");
            modelBuilder.Entity<Question>().HasKey(q => q.QuestionId);

            // Results table configuration
            modelBuilder.Entity<Results>().ToTable("Results");
            modelBuilder.Entity<Results>().HasKey(r => r.resultId);

            modelBuilder.Entity<Question>()
                .HasOne<Quiz>()
                .WithMany()
                .HasForeignKey(q => q.QuizId)
                .OnDelete(DeleteBehavior.Cascade);
            modelBuilder.Entity<Results>()
                .HasOne<IdentityUser>()
                .WithMany()
                .HasForeignKey(r => r.student_id)
                .OnDelete(DeleteBehavior.Restrict); // Restrict delete if student is deleted

            modelBuilder.Entity<Results>()
                .HasOne<Quiz>()
                .WithMany()
                .HasForeignKey(r => r.quiz_id)
                .OnDelete(DeleteBehavior.Cascade);
        }
    }

    public class Quiz
    {
        [Key]
        public int Id { get; set; }
        public string Quiz_name { get; set; }
        public int num { get; set; }
    }

    public class Question
    {
        [Key]
        public int QuestionId { get; set; }
        public string QuestionText { get; set; }
        public string Choice1 { get; set; }
        public string Choice2 { get; set; }
        public string Choice3 { get; set; }
        public string Choice4 { get; set; }
        public int Answer { get; set; }
        public int QuizId { get; set; }
    }

    public class Results
    {
        [Key]
        public int resultId { get; set; }
        public string student_id { get; set; }
        public int quiz_id { get; set; }
        public int score { get; set; }
        public DateTime time { get; set; }
    }
}
